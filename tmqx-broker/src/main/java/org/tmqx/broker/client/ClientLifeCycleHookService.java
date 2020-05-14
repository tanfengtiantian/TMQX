package org.tmqx.broker.client;

import io.netty.channel.Channel;
import org.tmqx.core.context.ContextManager;
import org.tmqx.core.remoting.ChannelEventListener;
import org.tmqx.core.remoting.ChannelEventType;


public class ClientLifeCycleHookService implements ChannelEventListener {

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {
        ContextManager.ListenerManager.notifyFinish(ChannelEventType.CONNECT,remoteAddr,channel);
    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {
        ContextManager.ListenerManager.notifyFinish(ChannelEventType.CLOSE,remoteAddr,channel);

    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {
        ContextManager.ListenerManager.notifyFinish(ChannelEventType.IDLE,remoteAddr,channel);

    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {
        ContextManager.ListenerManager.notifyFinish(ChannelEventType.EXCEPTION,remoteAddr,channel);
    }

}
