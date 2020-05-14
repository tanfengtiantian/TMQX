package org.tmqx.broker;

import org.tmqx.common.config.ApplicationConfig;
import org.tmqx.core.SnifferConfigInitializer;
import org.tmqx.core.boot.ServiceManager;
import org.tmqx.common.config.BrokerConfig;
import org.tmqx.common.config.NettyConfig;
import org.tmqx.core.plugin.PluginBootstrap;
import org.tmqx.core.plugin.PluginProtocol;

import java.util.Map;

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
        ApplicationConfig config = SnifferConfigInitializer.INSTANCE.initialize();
        BrokerConfig brokerConfig = config.getBroker();
        NettyConfig nettyConfig = config.getNetty();
        // 加载插件
        PluginBootstrap plugin = new PluginBootstrap();
        Map<String, PluginProtocol> mapPlugin = plugin.loadPlugins(config);

        // 初始化 服务管理
        ServiceManager.INSTANCE.boot();

        BrokerController brokerController = new BrokerController(brokerConfig, nettyConfig, mapPlugin);
        brokerController.start();


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            brokerController.shutdown();
            plugin.destroy();
            ServiceManager.INSTANCE.shutdown();
        }, "broker service shutdown thread"));

    }
}
