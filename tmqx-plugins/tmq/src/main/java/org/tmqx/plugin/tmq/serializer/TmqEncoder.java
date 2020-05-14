package org.tmqx.plugin.tmq.serializer;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.tmqx.plugin.tmq.bean.TmqMessage;

import java.util.List;

@ChannelHandler.Sharable
public class TmqEncoder extends MessageToMessageEncoder<TmqMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, TmqMessage msg, List<Object> out) throws Exception {

    }
}
