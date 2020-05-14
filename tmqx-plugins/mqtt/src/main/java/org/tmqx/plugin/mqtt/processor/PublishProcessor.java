package org.tmqx.plugin.mqtt.processor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmqx.plugin.mqtt.dispatcher.MessageDispatcher;
import org.tmqx.remoting.session.ClientSession;
import org.tmqx.remoting.session.ConnectManager;
import org.tmqx.plugin.mqtt.bean.Message;
import org.tmqx.plugin.mqtt.bean.MessageHeader;
import org.tmqx.plugin.mqtt.util.MessageUtil;
import org.tmqx.plugin.mqtt.util.NettyUtil;
import org.tmqx.remoting.netty.RequestProcessor;

import java.util.HashMap;
import java.util.Map;

public class PublishProcessor implements RequestProcessor {

    private static final Logger log = LoggerFactory.getLogger(PublishProcessor.class);

    private final MessageDispatcher messageDispatcher;

    public PublishProcessor(MessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }


    @Override
    public void processRequest(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        try{
            MqttPublishMessage publishMessage = (MqttPublishMessage) mqttMessage;
            MqttQoS qos = publishMessage.fixedHeader().qosLevel();
            String clientId = NettyUtil.getClientId(ctx.channel());
            ClientSession clientSession = ConnectManager.getInstance().getClient(clientId);
            String topic = publishMessage.variableHeader().topicName();
            //权限判断 暂不设置

            //innerMsg
            Message innerMsg = new Message();
            innerMsg.setPayload(MessageUtil.readBytesFromByteBuf(((MqttPublishMessage) mqttMessage).payload()));
            innerMsg.setClientId(clientId);
            innerMsg.setType(Message.Type.valueOf(mqttMessage.fixedHeader().messageType().value()));

            //headers
            Map<String,Object> headers = new HashMap<>();
            headers.put(MessageHeader.TOPIC,publishMessage.variableHeader().topicName());
            headers.put(MessageHeader.QOS,publishMessage.fixedHeader().qosLevel().value());
            headers.put(MessageHeader.RETAIN,publishMessage.fixedHeader().isRetain());
            headers.put(MessageHeader.DUP,publishMessage.fixedHeader().isDup());
            innerMsg.setHeaders(headers);
            innerMsg.setMsgId(publishMessage.variableHeader().packetId());

            switch (qos){
                case AT_MOST_ONCE:
                    processMessage(innerMsg);
                    break;
                case AT_LEAST_ONCE:
                    processQos1(ctx,innerMsg);
                    break;
                case EXACTLY_ONCE:
                    processQos2(ctx,innerMsg);
                    break;
                default:
                    log.warn("[PubMessage] -> Wrong mqtt message,clientId={}", clientId);
            }
        }catch (Throwable tr){
            log.warn("[PubMessage] -> Solve mqtt pub message exception:{}",tr);
        }finally {
            ReferenceCountUtil.release(mqttMessage.payload());
        }
    }

    private void processQos1(ChannelHandlerContext ctx, Message innerMsg) {

    }

    private void processQos2(ChannelHandlerContext ctx, Message innerMsg) {
    }

    protected void  processMessage(Message message){
        this.messageDispatcher.appendMessage(message);
        boolean retain = (boolean) message.getHeader(MessageHeader.RETAIN);
    }
}
