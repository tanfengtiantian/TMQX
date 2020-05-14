package org.tmqx.core.remoting;

import io.netty.channel.ChannelHandler;
import org.tmqx.common.config.NettyConfig;

public interface RemotingService {
    /**
     *  remoting init
     */
    void init(NettyConfig config, ChannelEventListener listener);

    /**
     * 设置协议处理内容
     */
    void setHandler(ChannelHandler handler);

    /**
     * remoting start
     */
    void start();

    /**
     * remoting shutdown
     */
    void shutdown();

}
