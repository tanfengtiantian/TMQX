package org.tmqx.plugin.mqtt.processor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import org.tmqx.plugin.mqtt.util.MessageUtil;
import org.tmqx.remoting.netty.RequestProcessor;

public class PingProcessor implements RequestProcessor {

    @Override
    public void processRequest(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        MqttMessage pingRespMessage = MessageUtil.getPingRespMessage();
        ctx.writeAndFlush(pingRespMessage);
    }
}
