package org.tmqx.core.context;

import io.netty.channel.Channel;
import org.tmqx.core.remoting.ChannelEventType;

/**
 * 连接监听接口
 * @author tf
 */
public interface ContextListener {

    void afterFinished(ChannelEventType type, String remoteAddr, Channel channel);
}
