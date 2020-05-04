package org.tmqx.plugin;


/**
 * 插件服务 (SPI, Singleton, ThreadSafe)
 */
public interface PluginAdapter {

    /**
     * 外部适配器初始化接口
     *
     */
    void init();
    /**
     * 适配器调用
     *
     */
    void invoke();

    /**
     * 外部适配器销毁接口
     */
    void destroy();

    /**
     * 调用方式
     */
    enum InvokeType {
        SYNC,  // 同步
        AYNC,  // 异步
        BATCH,  // 批量
        ;
        public static InvokeType parse(String name) {
            for (InvokeType s : values()) {
                if (s.name().equalsIgnoreCase(name)) {
                    return s;
                }
            }
            return null;
        }

        public static InvokeType getDefault() {
            return SYNC;
        }
    }
}
