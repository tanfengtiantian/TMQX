package org.tmqx.plugin.mqtt.subscribe;

import org.tmqx.plugin.mqtt.bean.Subscription;

import java.util.Set;

/**
 * Subscription 匹配器
 */
public interface SubscriptionMatcher {

    /**
     * add subscribe rlue 规则
     * @param subscription
     */
    boolean subscribe(Subscription subscription);

    boolean unSubscribe(String topic, String clientId);

    Set<Subscription> match(String topic);

    /**
     *
     * @param pubTopic
     * @param subTopic
     * @return
     */
    boolean isMatch(String pubTopic, String subTopic);
}
