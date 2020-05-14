package org.tmqx.common.config;

import java.util.Map;

public class PluginConfig {

    private String              name;       // 适配器名称, 如: mqtt

    private boolean             activation = false;

    private Map<String, String> properties; // 其余参数, 可填写适配器中的所需的配置信息

    private ApplicationConfig root;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }


    public ApplicationConfig getRoot() {
        return root;
    }

    public void setRoot(ApplicationConfig root) {
        this.root = root;
    }

    public boolean isActivation() {
        return activation;
    }

    public void setActivation(boolean activation) {
        this.activation = activation;
    }
}
