package org.tmqx.boot;

import java.util.Map;

/**
 * 容器服务 (SPI, Singleton, ThreadSafe)
 */
public interface BootService {

    /**
     * 初始化配置文件
     * @param config
     */
    void init(Map config);
    /**
     * 置前方法
     */
    void beforeBoot();

    /**
     * 主函数
     */
    void boot();

    /**
     * 置后方法
     */
    void afterBoot();

    /**
     * 关闭方法
     */
    void shutdown();

    /**
     *
     * @return
     */
    String bootName();
}
