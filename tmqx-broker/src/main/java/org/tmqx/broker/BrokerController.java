package org.tmqx.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmqx.broker.client.ClientLifeCycleHookService;
import org.tmqx.common.config.BrokerConfig;
import org.tmqx.common.config.NettyConfig;
import org.tmqx.core.plugin.PluginProtocol;
import org.tmqx.core.remoting.ChannelEventListener;
import org.tmqx.core.remoting.RemotingService;
import org.tmqx.remoting.netty.NettyRemotingServer;
import java.util.HashMap;
import java.util.Map;


public class BrokerController {

    private static final Logger log = LoggerFactory.getLogger(BrokerController.class);

    private final BrokerConfig brokerConfig;

    private final NettyConfig nettyConfig;

    private final ChannelEventListener channelEventListener;

    private Map<String,RemotingService> remotingServerMap = new HashMap<>();

    public BrokerController(BrokerConfig brokerConfig, NettyConfig nettyConfig, Map<String,PluginProtocol> mapPlugin) {
        this.brokerConfig = brokerConfig;
        this.nettyConfig = nettyConfig;
        this.channelEventListener = new ClientLifeCycleHookService();
        if(mapPlugin != null) {
            mapPlugin.forEach((k,v)->{
                RemotingService remotingService = new NettyRemotingServer(v);
                remotingService.init(nettyConfig,channelEventListener);
                remotingServerMap.put(k,remotingService);
            });
        }
    }

    public void start() {
        remotingServerMap.forEach((n,s)->{
            s.start();
            log.info("{} Server start success and version = {}", n, brokerConfig.getVersion());
        });
    }

    public void shutdown() {
        remotingServerMap.forEach((n,s)->{
            s.shutdown();
            log.info("{} Server shutdown ", n);
        });
    }
}
