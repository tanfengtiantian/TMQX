package org.tmqx.plugin.tmq;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmqx.common.config.PluginConfig;
import org.tmqx.common.support.SPI;
import org.tmqx.core.plugin.PluginProtocol;
import org.tmqx.plugin.tmq.serializer.TmqDecoder;
import org.tmqx.plugin.tmq.serializer.TmqEncoder;
import java.util.concurrent.RejectedExecutionException;


/**
 * tmq协议处理器
 *
 * @version 1.0.0
 */
@SPI("tmq")
public class TmqProtocol implements PluginProtocol {

    private static final Logger log = LoggerFactory.getLogger(TmqProtocol.class);

    @Override
    public void init(PluginConfig config) {

    }

    @Override
    public void start() {


    }

    @Override
    public void channelPipelineaddLast(ChannelPipeline pipeline) {
        pipeline.addLast("tmqEncoder", new TmqEncoder())
                .addLast("tmqDecoder", new TmqDecoder())
                .addLast("nettyTmqHandler", new NettyTmqHandler());
    }

    @Override
    public void destroy() {

    }

    class NettyTmqHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object obj){
            MqttMessage mqttMessage = (MqttMessage) obj;
            if(mqttMessage != null && mqttMessage.decoderResult().isSuccess()){
                MqttMessageType messageType = mqttMessage.fixedHeader().messageType();
                log.debug("[Remoting] -> receive tmq code,type:{}",messageType.value());
                //Runnable runnable = () -> processorTable.get(messageType).getObject1().processRequest(ctx,mqttMessage);
                try{
                    //processorTable.get(messageType).getObject2().submit(runnable);
                }catch (RejectedExecutionException ex){
                    log.warn("Reject tmq request,cause={}",ex.getMessage());
                }
            }else{
                ctx.close();
            }
        }
    }
}
