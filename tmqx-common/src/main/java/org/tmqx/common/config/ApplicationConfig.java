package org.tmqx.common.config;


import java.util.List;

public class ApplicationConfig {

    private String name;

    private BrokerConfig broker;

    private NettyConfig netty;

    private List<PluginConfig> plugins;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PluginConfig> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<PluginConfig> plugins) {
        this.plugins = plugins;
        if(plugins != null) {
            plugins.forEach(p-> p.setRoot(this));
        }
    }

    public NettyConfig getNetty() {
        return netty;
    }

    public void setNetty(NettyConfig netty) {
        this.netty = netty;
        if(netty != null) {
            netty.setRoot(this);
        }
    }

    public BrokerConfig getBroker() {
        return broker;
    }

    public void setBroker(BrokerConfig broker) {
        this.broker = broker;
    }
}
