package org.tmqx.plugin.mqtt.dispatcher;

import io.netty.handler.codec.mqtt.MqttPublishMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmqx.plugin.mqtt.bean.Subscription;
import org.tmqx.plugin.mqtt.subscribe.SubscriptionMatcher;
import org.tmqx.remoting.session.ClientSession;
import org.tmqx.remoting.session.ConnectManager;
import org.tmqx.plugin.mqtt.bean.Message;
import org.tmqx.plugin.mqtt.bean.MessageHeader;
import org.tmqx.plugin.mqtt.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;

public class DefaultDispatcherMessage implements MessageDispatcher {

    private static final Logger log = LoggerFactory.getLogger(MessageDispatcher.class);

    private final SubscriptionMatcher subscriptionMatcher;

    private final ThreadPoolExecutor pollThread;

    private volatile boolean stoped = false;

    private static final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>(100000);


    public DefaultDispatcherMessage(SubscriptionMatcher subscriptionMatcher, ThreadPoolExecutor pollThread) {
        this.subscriptionMatcher = subscriptionMatcher;
        this.pollThread = pollThread;
    }

    @Override
    public void start() {
        new Thread(() -> {
            int waitTime = 1000;
            while (!stoped) {
                try {
                    List<Message> messageList = new ArrayList(32);
                    Message message;
                    for (int i = 0; i < 32; i++) {
                        if (i == 0) {
                            message = messageQueue.poll(waitTime, TimeUnit.MILLISECONDS);
                        } else {
                            message = messageQueue.poll();
                        }
                        if (Objects.nonNull(message)) {
                            messageList.add(message);
                        } else {
                            break;
                        }
                    }
                    if (messageList.size() > 0) {
                        AsyncDispatcher dispatcher = new AsyncDispatcher(messageList);
                        pollThread.submit(dispatcher).get();
                    }
                } catch (InterruptedException e) {
                    log.warn("poll message wrong.");
                } catch (ExecutionException e) {
                    log.warn("AsyncDispatcher get() wrong.");
                }
            }
        }).start();
    }

    @Override
    public void shutdown() {
        this.stoped = true;
    }

    @Override
    public boolean appendMessage(Message message) {
        boolean isNotFull = messageQueue.offer(message);
        if (!isNotFull) {
            log.warn("[PubMessage] -> the buffer queue is full");
        }
        return isNotFull;
    }


    class AsyncDispatcher implements Runnable {

        private List<Message> messages;

        AsyncDispatcher(List<Message> messages) {
            this.messages = messages;
        }

        @Override
        public void run() {
            if (Objects.nonNull(messages)) {
                try {
                    for (Message message : messages) {
                        Set<Subscription> subscriptions = subscriptionMatcher.match((String) message.getHeader(MessageHeader.TOPIC));
                        for (Subscription subscription : subscriptions) {
                            String clientId = subscription.getClientId();
                            ClientSession clientSession = ConnectManager.getInstance().getClient(subscription.getClientId());
                            if (ConnectManager.getInstance().containClient(clientId)) {
                                int qos = MessageUtil.getMinQos((int) message.getHeader(MessageHeader.QOS), subscription.getQos());
                                int messageId = clientSession.generateMessageId();
                                message.putHeader(MessageHeader.QOS, qos);
                                message.setMsgId(messageId);
                                MqttPublishMessage publishMessage = MessageUtil.getPubMessage(message, false, qos, messageId);
                                clientSession.getCtx().writeAndFlush(publishMessage);
                            } else {
                                //离线消息是否存储
                            }
                        }
                    }
                } catch (Exception ex) {
                    log.warn("Dispatcher message failure,cause={}", ex);
                }
            }
        }

    }
}
