package org.tmqx.broker;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Consumer {
    private static final String broker = "tcp://127.0.0.1:1883";
    private static final String topic = "MQTT/+";
    private static final String clientId = "MQTT_SUB_CLIENT";

    public static void main(String[] args) throws MqttException, InterruptedException {
        MqttClient subClient = getMqttClient();
        subClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                System.out.println("Connect lost,do some thing to solve it");
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                System.out.println("From topic: " + s);
                System.out.println("Message content: " + new String(mqttMessage.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        subClient.subscribe(topic);
    }


    private static MqttClient getMqttClient(){
        try {
            MqttClient pubClient = new MqttClient(broker,clientId,new MemoryPersistence());
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            pubClient.connect(connectOptions);
            return pubClient;
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return null;
    }
}
