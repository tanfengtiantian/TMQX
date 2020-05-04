package org.tmqx.context;

import org.tmqx.boot.BootService;
import org.tmqx.boot.ServiceManager;
import org.tmqx.remoting.RemotingService;

import java.util.*;

public class RemotingManager implements BootService {

    private String bootName = "netty";

    private Map config;

    private RemotingService service;


    private Map<Class, RemotingService> bootedServices = new HashMap<>();

    @Override
    public void init(Map config) {
        this.config = config;
        ServiceLoader<RemotingService> bootServices = ServiceLoader.load(RemotingService.class);
        for (RemotingService bootService : bootServices) {
            this.bootedServices.put(bootService.getClass(), bootService);
        }

    }

    @Override
    public void beforeBoot() {

        System.out.println("RemotingManager beforeBoot");
    }

    @Override
    public void boot() {

    }

    @Override
    public void afterBoot() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public String bootName() {
        return bootName;
    }

    public static class ListenerManager {

        private static List<Object> LISTENERS = new LinkedList<>();

        public static synchronized void add(Object listener) {
            LISTENERS.add(listener);
        }

        static void notifyFinish(Object segment) {
            for (Object listener : LISTENERS) {
                //listener.afterFinished(segment);
            }
        }

        public static synchronized void remove(Object listener) {
            LISTENERS.remove(listener);
        }
    }
}
