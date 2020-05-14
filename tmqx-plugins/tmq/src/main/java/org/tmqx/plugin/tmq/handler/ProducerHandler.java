package org.tmqx.plugin.tmq.handler;

import org.tmqx.plugin.tmq.bean.Request;
import org.tmqx.plugin.tmq.log.ILogManager;
import org.tmqx.plugin.tmq.processor.RequestKeys;
import java.io.IOException;

public class ProducerHandler extends AbstractHandler{

    public ProducerHandler(ILogManager logManager) {
        super(logManager);
    }

    @Override
    public void handler(RequestKeys requestType, Request request) throws IOException {

    }
}
