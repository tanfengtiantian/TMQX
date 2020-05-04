package org.tmqx.remoting;

import org.tmqx.common.config.RemotingConfig;

public interface RemotingService {
    /**
     *  remoting init
     */
    void init(RemotingConfig config);
    /**
     * remoting start
     */
    void start();

    /**
     * remoting shutdown
     */
    void shutdown();

}
