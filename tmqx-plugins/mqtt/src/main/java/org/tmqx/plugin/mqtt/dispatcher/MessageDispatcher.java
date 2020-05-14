package org.tmqx.plugin.mqtt.dispatcher;

import org.tmqx.plugin.mqtt.bean.Message;

public interface MessageDispatcher {

    void start();

    void shutdown();

    boolean appendMessage(Message message);

}
