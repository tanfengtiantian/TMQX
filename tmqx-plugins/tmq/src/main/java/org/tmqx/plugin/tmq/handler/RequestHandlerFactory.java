package org.tmqx.plugin.tmq.handler;

import org.tmqx.plugin.tmq.processor.RequestKeys;

/**
 * @author tf
 * @version 创建时间：2019年1月17日 下午2:09:03
 * @ClassName 用于创建请求处理程序的工厂
 */
public interface RequestHandlerFactory {
    /**
     * 为请求映射请求处理程序
     *
     * @param id request type
     * @return handler for the request
     */
    RequestHandler mapping(RequestKeys id);
}
