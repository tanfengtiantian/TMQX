package org.tmqx.plugin.tmq.processor;

public interface RequestProcessor {
    /**
     * request type
     *
     */
    RequestKeys getRequestKey();
}
