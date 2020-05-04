package org.tmqx.context;

import org.tmqx.boot.BootService;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ContextManager implements BootService {

    private String bootName = "context";

    private Map config;

    @Override
    public void init(Map config) {
        this.config = config;
    }

    @Override
    public void beforeBoot() {
        System.out.println("ContextManager beforeBoot");
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
