package org.tmqx.boot;

import org.tmqx.common.config.ApplicationConfig;
import org.tmqx.common.config.SnifferConfigInitializer;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 基于SPI机制，服务管理
 */
public enum ServiceManager {
    INSTANCE;

    public void boot() {
        loadAllServices()
                  .init()
            .beforeBoot()
               .startup()
             .afterBoot();
    }

    private Map<String, BootService> bootedServices = new HashMap<>();

    /**
     * 基于SPI机制，内部ServiceLoader加载 BootService
     */
    private ServiceManager loadAllServices() {
        ServiceLoader<BootService> bootServices = ServiceLoader.load(BootService.class);
        for (BootService bootService : bootServices) {
            this.bootedServices.put(bootService.bootName(), bootService);
        }
        return this;
    }

    /**
     * 启动前
     */
    private ServiceManager init() {
        for (BootService service : bootedServices.values()) {
            service.init(SnifferConfigInitializer.getYamlMap(service.bootName()+ ApplicationConfig.FileSuffix));
        }
        return this;
    }
    /**
     * 启动前
     */
    private ServiceManager beforeBoot() {
        for (BootService service : bootedServices.values()) {
            service.beforeBoot();
        }
        return this;
    }

    /**
     * 启动
     */
    private ServiceManager startup() {
        for (BootService service : bootedServices.values()) {
            service.boot();
        }
        return this;
    }

    /**
     * 启动后
     */
    private ServiceManager afterBoot() {
        for (BootService service : bootedServices.values()) {
            service.afterBoot();
        }
        return this;
    }

    /**
     * 查找服务
     */
    public <T extends BootService> T findService(Class<T> serviceClass) {
        return (T)bootedServices.get(serviceClass);
    }

    /**
     * 关闭服务
     */
    public void shutdown() {
        for (BootService service : bootedServices.values()) {
            service.shutdown();
        }
    }
}