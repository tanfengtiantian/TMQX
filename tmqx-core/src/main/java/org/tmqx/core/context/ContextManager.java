package org.tmqx.core.context;

import io.netty.channel.Channel;
import org.tmqx.core.boot.BootService;
import org.tmqx.core.remoting.ChannelEventType;
import java.util.LinkedList;
import java.util.List;

public class ContextManager implements BootService ,ContextListener {

    private String bootName = "context";

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

    public static class ListenerManager {

        private static List<ContextListener> LISTENERS = new LinkedList<>();

        public static synchronized void add(ContextListener listener) {
            LISTENERS.add(listener);
        }

        public static void notifyFinish(ChannelEventType type, String remoteAddr, Channel channel) {
            for (ContextListener listener : LISTENERS) {
                listener.afterFinished(type,remoteAddr,channel);
            }
        }

        public static synchronized void remove(Object listener) {
            LISTENERS.remove(listener);
        }
    }
}
