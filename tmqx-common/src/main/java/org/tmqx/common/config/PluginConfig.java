package org.tmqx.common.config;

import java.util.Map;

public class PluginConfig {

    private String              name;       // 适配器名称, 如: logger, mqtt, es

    private Map<String, String> properties; // 其余参数, 可填写适配器中的所需的配置信息

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


}
