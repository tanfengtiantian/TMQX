package org.tmqx.broker;

import org.tmqx.boot.ServiceManager;
import org.tmqx.common.config.SnifferConfigInitializer;

public class BrokerStartup {

    public static void main(String[] args) {
        try {
            start(args);
        } catch (Exception e) {
            System.out.println("tmqx start failure,cause = " + e);
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void start(String[] args) throws Exception{
        // 初始化 配置CommandLine

        //初始化 配置文件
        SnifferConfigInitializer.initialize();
        // 加载插件
        //pluginFinder = new PluginFinder(new PluginBootstrap().loadPlugins());
        // 初始化 服务管理
        ServiceManager.INSTANCE.boot();

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                ServiceManager.INSTANCE.shutdown(),
                "Broker service shutdown thread")
        );

    }
}
