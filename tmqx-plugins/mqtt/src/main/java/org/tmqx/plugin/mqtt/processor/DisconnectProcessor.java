package org.tmqx.plugin.mqtt.processor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmqx.remoting.netty.RequestProcessor;

public class DisconnectProcessor implements RequestProcessor {

    private static final Logger log = LoggerFactory.getLogger(DisconnectProcessor.class);

    @Override
    public void processRequest(ChannelHandlerContext ctx, MqttMessage mqttMessage) {

        log.info("Disconnect!!!!");
    }
}
