package org.tmqx.plugin.mqtt.processor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmqx.plugin.mqtt.subscribe.SubscriptionMatcher;
import org.tmqx.remoting.session.ClientSession;
import org.tmqx.remoting.session.ConnectManager;
import org.tmqx.plugin.mqtt.util.NettyUtil;
import org.tmqx.plugin.mqtt.util.MessageUtil;
import org.tmqx.remoting.netty.RequestProcessor;

import java.util.List;
import java.util.Objects;

public class UnSubscribeProcessor implements RequestProcessor {

    private Logger log = LoggerFactory.getLogger(UnSubscribeProcessor.class);

    private SubscriptionMatcher subscriptionMatcher;

    public UnSubscribeProcessor(SubscriptionMatcher subscriptionMatcher){
        this.subscriptionMatcher = subscriptionMatcher;
    }

    @Override
    public void processRequest(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        MqttUnsubscribeMessage unsubscribeMessage = (MqttUnsubscribeMessage) mqttMessage;
        MqttUnsubscribePayload unsubscribePayload = unsubscribeMessage.payload();
        List<String> topics = unsubscribePayload.topics();
        String clientId = NettyUtil.getClientId(ctx.channel());
        ClientSession clientSession = ConnectManager.getInstance().getClient(clientId);
        if(Objects.isNull(clientSession)){
            log.warn("[UnSubscribe] -> The client is not online.clientId={}",clientId);
        }
        topics.forEach( topic -> {
            subscriptionMatcher.unSubscribe(topic,clientId);
        });
        MqttUnsubAckMessage unsubAckMessage = MessageUtil.getUnSubAckMessage(MessageUtil.getMessageId(mqttMessage));
        ctx.writeAndFlush(unsubAckMessage);
    }
}
