package org.tmqx.plugin.tmq.handler;

import org.tmqx.plugin.tmq.bean.Request;
import org.tmqx.plugin.tmq.processor.RequestKeys;

import java.io.IOException;

public interface RequestHandler {

    /**
     * 处理请求
     *
     * @param requestType
     * @param request
     * @return 处理响应
     */
    void handler(RequestKeys requestType, Request request) throws IOException;
}
