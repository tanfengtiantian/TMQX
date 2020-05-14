package org.tmqx.core.plugin;


import io.netty.channel.ChannelPipeline;
import org.tmqx.common.config.PluginConfig;
import org.tmqx.common.support.SPI;

/**
 * 插件服务 (SPI, Singleton, ThreadSafe)
 */
@SPI
public interface PluginProtocol{

    /**
     * 外部适配器初始化接口
     *
     */
    void init(PluginConfig config);
    /**
     * 适配器启动
     *
     */
    void start();


    void channelPipelineaddLast(ChannelPipeline pipeline);

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
