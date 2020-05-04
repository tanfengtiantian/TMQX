package org.tmqx.plugin;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginBootstrap {

    private static final Logger log = LoggerFactory.getLogger(PluginBootstrap.class);


    private volatile boolean running = false;


    public synchronized void init() {


    }

    public synchronized void destroy() {

    }
}
