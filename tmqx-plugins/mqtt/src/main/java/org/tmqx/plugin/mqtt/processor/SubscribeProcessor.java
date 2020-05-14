package org.tmqx.plugin.mqtt.processor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmqx.plugin.mqtt.bean.Subscription;
import org.tmqx.plugin.mqtt.bean.Topic;
import org.tmqx.plugin.mqtt.subscribe.SubscriptionMatcher;
import org.tmqx.remoting.session.ClientSession;
import org.tmqx.remoting.session.ConnectManager;
import org.tmqx.plugin.mqtt.util.MessageUtil;
import org.tmqx.plugin.mqtt.util.NettyUtil;
import org.tmqx.remoting.netty.RequestProcessor;

import java.util.ArrayList;
import java.util.List;

public class SubscribeProcessor implements RequestProcessor {

    private static final Logger log = LoggerFactory.getLogger(SubscribeProcessor.class);

    private final SubscriptionMatcher subscriptionMatcher;

    public SubscribeProcessor(SubscriptionMatcher subscriptionMatcher) {
        this.subscriptionMatcher = subscriptionMatcher;
    }
    @Override
    public void processRequest(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        MqttSubscribeMessage subscribeMessage = (MqttSubscribeMessage) mqttMessage;
        String clientId = NettyUtil.getClientId(ctx.channel());
        int messageId = subscribeMessage.variableHeader().messageId();
        ClientSession clientSession = ConnectManager.getInstance().getClient(clientId);
        List<Topic> validTopicList =validTopics(clientSession,subscribeMessage.payload().topicSubscriptions());
        if(validTopicList == null || validTopicList.size() == 0){
            log.warn("[Subscribe] -> Valid all subscribe topic failure,clientId:{}",clientId);
            return;
        }
        //获取QOS
        List<Integer> ackQos = getTopicQos(validTopicList);
        MqttMessage subAckMessage = MessageUtil.getSubAckMessage(messageId,ackQos);
        ctx.writeAndFlush(subAckMessage);

        // 订阅管理
        subscribe(clientSession,validTopicList);
        //集群处理
    }



    /**
     * 返回校验合法的topic
     */
    private List<Topic> validTopics(ClientSession clientSession, List<MqttTopicSubscription> topics){
        List<Topic> topicList = new ArrayList<>();
        for(MqttTopicSubscription subscription : topics){
            //订阅权限，客户端是否能订阅此topic，暂无

            //添加订阅列表
            Topic topic = new Topic(subscription.topicName(),subscription.qualityOfService().value());
            topicList.add(topic);
        }
        return topicList;
    }

    private List<Integer> getTopicQos(List<Topic> topics){
        List<Integer> qoss = new ArrayList<>(topics.size());
        for(Topic topic : topics){
            qoss.add(topic.getQos());
        }
        return qoss;
    }

    private void subscribe(ClientSession clientSession,List<Topic> validTopicList){
        for(Topic topic : validTopicList){
            Subscription subscription = new Subscription(clientSession.getClientId(),topic.getTopicName(),topic.getQos());
            this.subscriptionMatcher.subscribe(subscription);
        }
    }
}
