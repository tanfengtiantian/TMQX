package org.tmqx.core.plugin;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmqx.common.config.ApplicationConfig;
import org.tmqx.common.config.PluginConfig;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginBootstrap {

    private static final Logger log = LoggerFactory.getLogger(PluginBootstrap.class);

    private ExtensionLoader<PluginProtocol> loader;

    private Map<String,PluginProtocol> connectors = new HashMap<>();

    public synchronized void destroy() {
        connectors.forEach((k,v) -> v.destroy());
    }

    public synchronized Map<String,PluginProtocol> loadPlugins(ApplicationConfig applicationConfig) throws FileNotFoundException {
        loader = ExtensionLoader.getExtensionLoader(PluginProtocol.class);
        //配置文件
        List<PluginConfig> listPlugin = applicationConfig.getPlugins();
        //end
        for (PluginConfig p : listPlugin){
            if(p.isActivation()){
                loadConnector(p, connectors);
                break;
            }
        }
        return connectors;
    }

    private void loadConnector(PluginConfig config,
                               Map<String,PluginProtocol> connectors) {
        try {

            PluginProtocol plugin;
            plugin = loader.getExtension(config.getName(), StringUtils.trimToEmpty(config.getName()));

            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            // 替换ClassLoader
            Thread.currentThread().setContextClassLoader(plugin.getClass().getClassLoader());
            plugin.init(config);
            Thread.currentThread().setContextClassLoader(cl);
            connectors.put(config.getName(),plugin);

            log.info("Load PluginProcessor : {} succeed", config.getName());
        } catch (Exception e) {
            log.error("Load PluginProcessor: {} failed", config.getName(), e);
        }
    }
}
