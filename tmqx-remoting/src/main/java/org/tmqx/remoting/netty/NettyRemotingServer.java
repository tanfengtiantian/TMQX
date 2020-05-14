package org.tmqx.remoting.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmqx.common.config.BrokerConfig;
import org.tmqx.common.config.NettyConfig;
import org.tmqx.common.helper.ThreadFactoryImpl;
import org.tmqx.core.plugin.PluginProtocol;
import org.tmqx.core.remoting.ChannelEventListener;
import org.tmqx.core.remoting.RemotingService;


public class NettyRemotingServer implements RemotingService {

    private static final Logger log = LoggerFactory.getLogger(NettyRemotingServer.class);
    private NettyConfig nettyConfig;
    private EventLoopGroup selectorGroup;
    private EventLoopGroup ioGroup;
    private Class<? extends ServerChannel> clazz;
    private NettyEventExcutor nettyEventExcutor;
    private BrokerConfig brokerConfig;
    private PluginProtocol processor;

    public NettyRemotingServer(PluginProtocol processor) {
        this.processor = processor;
    }

    @Override
    public void init(NettyConfig nettyConfig, ChannelEventListener listener) {
        this.nettyConfig = nettyConfig;
        this.brokerConfig = nettyConfig.getRoot().getBroker();
        this.nettyEventExcutor = new NettyEventExcutor(listener);

        if(!nettyConfig.isUseEpoll()){
            selectorGroup = new NioEventLoopGroup(nettyConfig.getSelectorThreadNum(),
                    new ThreadFactoryImpl("SelectorEventGroup"));
            ioGroup = new NioEventLoopGroup(nettyConfig.getIoThreadNum(),
                    new ThreadFactoryImpl("IOEventGroup"));
            clazz = NioServerSocketChannel.class;
        }else{
            selectorGroup = new EpollEventLoopGroup(nettyConfig.getSelectorThreadNum(),
                    new ThreadFactoryImpl("SelectorEventGroup"));
            ioGroup = new EpollEventLoopGroup(nettyConfig.getIoThreadNum(),
                    new ThreadFactoryImpl("IOEventGroup"));
            clazz = EpollServerSocketChannel.class;
        }
    }

    @Override
    public void setHandler(ChannelHandler handler) {

    }

    @Override
    public void start() {
        //processor
        processor.start();
        //Netty event excutor start
        this.nettyEventExcutor.start();
        // start TCP server
        if (nettyConfig.isStartTcp()) {
            startTcpServer(false, nettyConfig.getTcpPort());
        }

        if (nettyConfig.isStartSslTcp()) {
            startTcpServer(true, nettyConfig.getSslTcpPort());
        }

        /*
        // start Websocket server
        if (nettyConfig.isStartWebsocket()) {
            startWebsocketServer(false, nettyConfig.getWebsocketPort());
        }

        if (nettyConfig.isStartSslWebsocket()) {
            startWebsocketServer(true, nettyConfig.getSslWebsocketPort());
        }
         */
    }

    private void startTcpServer(boolean useSsl, Integer port){
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(selectorGroup,ioGroup)
                .channel(clazz)
                .option(ChannelOption.SO_BACKLOG, nettyConfig.getTcpBackLog())
                .childOption(ChannelOption.TCP_NODELAY, nettyConfig.isTcpNoDelay())
                .childOption(ChannelOption.SO_SNDBUF, nettyConfig.getTcpSndBuf())
                .option(ChannelOption.SO_RCVBUF, nettyConfig.getTcpRcvBuf())
                .option(ChannelOption.SO_REUSEADDR, nettyConfig.isTcpReuseAddr())
                .childOption(ChannelOption.SO_KEEPALIVE, nettyConfig.isTcpKeepAlive())
                .childHandler(new DefaultChannelInitializer(useSsl));
        if(nettyConfig.isPooledByteBufAllocatorEnable()){
            bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        }
        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            log.info("Start tcp server {} success,port = {}", useSsl?"with ssl":"", port);
        }catch (InterruptedException ex){
            log.error("Start tcp server {} failure.cause={}", useSsl?"with ssl":"", ex);
        }
    }


    @Override
    public void shutdown() {
        if (selectorGroup != null) {
            selectorGroup.shutdownGracefully();
        }
        if (ioGroup != null) {
            ioGroup.shutdownGracefully();
        }
    }

    class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {

        boolean useSsl;

        public DefaultChannelInitializer(boolean useSsl) {
            this.useSsl = useSsl;
        }

        @Override
        protected void initChannel(SocketChannel socketChannel) {
            ChannelPipeline pipeline = socketChannel.pipeline();
            if (useSsl) {
                pipeline.addLast("ssl", NettySslHandler.getSslHandler(
                        socketChannel,
                        nettyConfig.isUseClientCA(),
                        nettyConfig.getSslKeyStoreType(),
                        brokerConfig.getTmqxHome() + nettyConfig.getSslKeyFilePath(),
                        nettyConfig.getSslManagerPwd(),
                        nettyConfig.getSslStorePwd()
                ));
            }
            pipeline.addLast("idleStateHandler", new IdleStateHandler(60, 0, 0))
                    .addLast("nettyConnectionManager", new NettyConnectHandler(nettyEventExcutor));
            //插件自定义处理器
            processor.channelPipelineaddLast(pipeline);
        }
    }
}
