package org.tmqx.core.context;

import io.netty.channel.Channel;
import org.tmqx.core.boot.BootService;
import org.tmqx.core.remoting.ChannelEventType;
import org.tmqx.core.remoting.RemotingService;

import java.util.*;

public class RemotingManager implements BootService,ContextListener {

    private String bootName = "netty";

    private Map config;

    private RemotingService service;


    private Map<Class, RemotingService> bootedServices = new HashMap<>();

    @Override
    public void init() {

    }

    @Override
    public void beforeBoot() {
        ContextManager.ListenerManager.add(this);
    }

    @Override
    public void boot() {

    }

    @Override
    public void afterBoot() {

    }

    @Override
    public void shutdown() {
        ContextManager.ListenerManager.remove(this);
    }

    @Override
    public String bootName() {
        return bootName;
    }

    @Override
    public void afterFinished(ChannelEventType type, String remoteAddr, Channel channel) {

    }
}
