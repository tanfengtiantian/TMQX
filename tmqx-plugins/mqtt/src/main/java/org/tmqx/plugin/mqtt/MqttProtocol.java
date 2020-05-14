package org.tmqx.plugin.mqtt;

import io.netty.channel.*;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmqx.common.config.PluginConfig;
import org.tmqx.common.helper.Pair;
import org.tmqx.common.helper.RejectHandler;
import org.tmqx.common.helper.ThreadFactoryImpl;
import org.tmqx.common.support.SPI;
import org.tmqx.plugin.mqtt.dispatcher.DefaultDispatcherMessage;
import org.tmqx.plugin.mqtt.dispatcher.MessageDispatcher;
import org.tmqx.plugin.mqtt.processor.*;
import org.tmqx.plugin.mqtt.subscribe.DefaultSubscriptionTreeMatcher;
import org.tmqx.plugin.mqtt.subscribe.SubscriptionMatcher;
import org.tmqx.core.plugin.PluginProtocol;
import org.tmqx.remoting.netty.RequestProcessor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * mqtt协议处理器
 *
 * @version 1.0.0
 */
@SPI("mqtt")
public class MqttProtocol implements PluginProtocol {

    private static final Logger log = LoggerFactory.getLogger(MqttProtocol.class);
    //消息派发器
    private MessageDispatcher messageDispatcher;
    //订阅匹配器
    private SubscriptionMatcher subscriptionMatcher;
    private ExecutorService connectExecutor;
    private ExecutorService pubExecutor;
    private ExecutorService pingExecutor;
    private ExecutorService subExecutor;
    private ThreadPoolExecutor pollThread;
    private Map<MqttMessageType, Pair<RequestProcessor, ExecutorService>> processorTable = new HashMap();

    private int coreThreadNum = Runtime.getRuntime().availableProcessors() * 2;
    private int keepAliveTime = 60000;
    private int queueCapacity = 1000;
    private int maxBlockQueueSize = 1000;
    private int maxBytesInMessage = 512*1024;

    @Override
    public void init(PluginConfig config) {
        //config init
        coreThreadNum = Integer.parseInt(StringUtils.defaultString(config.getProperties().get("mqtt.pool.coreThreadNum"),coreThreadNum+""));
        keepAliveTime = Integer.parseInt(StringUtils.defaultString(config.getProperties().get("mqtt.pool.keepAliveTime"),keepAliveTime+""));
        queueCapacity = Integer.parseInt(StringUtils.defaultString(config.getProperties().get("mqtt.pool.queueCapacity"),queueCapacity+""));
        maxBlockQueueSize = Integer.parseInt(StringUtils.defaultString(config.getProperties().get("mqtt.pool.maxBlockQueueSize"),maxBlockQueueSize+""));
        maxBytesInMessage = Integer.parseInt(StringUtils.defaultString(config.getProperties().get("mqtt.pool.maxBytesInMessage"),maxBytesInMessage+""));

        this.pollThread = new ThreadPoolExecutor(coreThreadNum,
                coreThreadNum,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadFactoryImpl("pollMessage2Subscriber"),
                new RejectHandler("pollMessage", maxBlockQueueSize));

        this.connectExecutor = new ThreadPoolExecutor(coreThreadNum,
                coreThreadNum,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue(queueCapacity),
                new ThreadFactoryImpl("ConnectThread"),
                new RejectHandler("connect", maxBlockQueueSize));
        this.pubExecutor = new ThreadPoolExecutor(coreThreadNum,
                coreThreadNum,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue(queueCapacity),
                new ThreadFactoryImpl("PubThread"),
                new RejectHandler("pub", maxBlockQueueSize));

        this.pingExecutor = new ThreadPoolExecutor(coreThreadNum,
                coreThreadNum,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue(queueCapacity),
                new ThreadFactoryImpl("PingThread"),
                new RejectHandler("heartbeat", maxBlockQueueSize));

        this.subExecutor = new ThreadPoolExecutor(coreThreadNum,
                coreThreadNum,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue(queueCapacity),
                new ThreadFactoryImpl("SubThread"),
                new RejectHandler("sub", maxBlockQueueSize));

        subscriptionMatcher = new DefaultSubscriptionTreeMatcher();
        messageDispatcher = new DefaultDispatcherMessage(subscriptionMatcher,pollThread);

        //注册协议
        {//init and register mqtt remoting processor
            RequestProcessor connectProcessor = new ConnectProcessor();
            RequestProcessor pingProcessor = new PingProcessor();
            RequestProcessor disconnectProcessor = new DisconnectProcessor();
            RequestProcessor publishProcessor = new PublishProcessor(messageDispatcher);
            RequestProcessor subscribeProcessor = new SubscribeProcessor(subscriptionMatcher);
            RequestProcessor unSubscribeProcessor = new UnSubscribeProcessor(subscriptionMatcher);

            this.registerProcessor(MqttMessageType.CONNECT, connectProcessor, connectExecutor);
            this.registerProcessor(MqttMessageType.DISCONNECT, disconnectProcessor, connectExecutor);
            this.registerProcessor(MqttMessageType.PINGREQ, pingProcessor, pingExecutor);
            this.registerProcessor(MqttMessageType.PUBLISH, publishProcessor, pubExecutor);
            this.registerProcessor(MqttMessageType.SUBSCRIBE, subscribeProcessor, subExecutor);
            this.registerProcessor(MqttMessageType.UNSUBSCRIBE, unSubscribeProcessor, subExecutor);
        }
        log.info("MqttProcessor init!!");
    }

    public void registerProcessor(MqttMessageType mqttType, RequestProcessor processor, ExecutorService executorService){
        this.processorTable.put(mqttType,new Pair<>(processor,executorService));
    }

    @Override
    public void start() {
        messageDispatcher.start();
    }

    @Override
    public void channelPipelineaddLast(ChannelPipeline pipeline) {
        pipeline.addLast("mqttEncoder", MqttEncoder.INSTANCE)
                .addLast("mqttDecoder", new MqttDecoder(maxBytesInMessage))
                .addLast("nettyMqttHandler", new NettyMqttHandler());
    }


    @Override
    public void destroy() {
        processorTable.forEach((k,v)->{
            v.getObject2().shutdown();
            log.info("MqttMessageType = {} ,RequestProcessor shutdown",k.name());
        });
    }

    class NettyMqttHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object obj){
            MqttMessage mqttMessage = (MqttMessage) obj;
            if(mqttMessage != null && mqttMessage.decoderResult().isSuccess()){
                MqttMessageType messageType = mqttMessage.fixedHeader().messageType();
                log.debug("[Remoting] -> receive mqtt code,type:{}",messageType.value());
                Runnable runnable = () -> processorTable.get(messageType).getObject1().processRequest(ctx,mqttMessage);
                try{
                    processorTable.get(messageType).getObject2().submit(runnable);
                }catch (RejectedExecutionException ex){
                    log.warn("Reject mqtt request,cause={}",ex.getMessage());
                }
            }else{
                ctx.close();
            }
        }
    }
}
