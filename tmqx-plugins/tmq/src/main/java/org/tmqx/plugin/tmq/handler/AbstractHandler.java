package org.tmqx.plugin.tmq.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmqx.plugin.tmq.log.ILogManager;

public abstract class AbstractHandler implements RequestHandler {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final ILogManager logManager;

    public AbstractHandler(ILogManager logManager) {
        this.logManager = logManager;
    }
}
