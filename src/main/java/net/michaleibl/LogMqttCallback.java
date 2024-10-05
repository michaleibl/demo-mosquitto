package net.michaleibl;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

@Slf4j
public class LogMqttCallback implements MqttCallback {
    @Override
    public void connectionLost(Throwable e) {
        log.error("Connection lost: {}", e.getMessage(), e);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        log.info("Received a message from topic {}: {}", topic, message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.info("Delivery completed: {}. ", token.isComplete());
    }
}
