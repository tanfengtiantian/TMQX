package org.tmqx.plugin.tmq.bean;

import org.tmqx.plugin.tmq.processor.RequestKeys;


public interface Request{

    /**
     * request type
     *
     */
    RequestKeys getRequestKey();

}
