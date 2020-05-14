package org.tmqx.remoting.netty;

import io.netty.channel.Channel;
import org.tmqx.core.remoting.ChannelEventType;

public class NettyEvent {

    private String remoteAddr;
    private ChannelEventType eventType;
    private Channel channel;

    public NettyEvent(String remoteAddr, ChannelEventType eventType, Channel channel) {
        this.remoteAddr = remoteAddr;
        this.eventType = eventType;
        this.channel = channel;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public ChannelEventType getEventType() {
        return eventType;
    }

    public Channel getChannel() {
        return channel;
    }
}
