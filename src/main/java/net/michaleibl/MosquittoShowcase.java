package net.michaleibl;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

@Slf4j
public class MosquittoShowcase {
    private static final String BROKER_URI = "tcp://localhost:1883";
    private static final String CLIENT_ID = MosquittoShowcase.class.getSimpleName();
    private static final String TOPIC = "test/topic";
    private static final String CONTENT = "Hello from Java - %d";
    private static final int QUALITY_OF_SERVICE = 2;

    public static void main(String[] args) {
        System.out.println("Mosquitto Showcase started");

        final var brokerHostname = args.length == 1 ? args[0] : System.getenv("MQTT_BROKER_HOSTNAME");
        final var brokerUri = null == brokerHostname ? BROKER_URI : "tcp://" + brokerHostname + ":1883";

        MqttClient client;
        try {
            log.info("Creating the client ...");
            client = new MqttClient(brokerUri, CLIENT_ID, new MemoryPersistence());
        } catch (MqttException e) {
            log.error("Cannot connect to broker {}: {}", brokerUri, e.getMessage(), e);
            return;
        }
        client.setCallback(new LogMqttCallback());

        try {
            log.info("Connecting to broker {} ...", brokerUri);
            client.connect();
            log.info("Client connected");
        } catch (MqttException e) {
            log.error("Cannot connect to broker {}: {}", brokerUri, e.getMessage(), e);
            return;
        }

        try {
            log.info("Subscribing to topic {} ...", TOPIC);
            client.subscribe(TOPIC, QUALITY_OF_SERVICE);
            log.info("Client subscribed.");
        } catch (MqttException e) {
          log.error("Cannot subscribe to {}: {}", TOPIC, e.getMessage(), e);
        }

        try {
            log.info("Publishing messages to topic {} ...", TOPIC);

            for (int i = 1; i <= 5; i++) {
                var message = new MqttMessage(String.format(CONTENT, i).getBytes());
                message.setQos(QUALITY_OF_SERVICE);
                client.publish(TOPIC, message);
                log.info("Message {} published.", i);
            }
        } catch (MqttPersistenceException e) {
            log.error("Cannot persist the message to {}: {}", TOPIC, e.getMessage(), e);
        } catch (MqttException e) {
            log.error("Cannot publish the message to {}: {}", TOPIC, e.getMessage(), e);
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            log.warn("Sleeping interrupted!", e);
        }

        try {
            client.disconnect();
            System.out.println("Disconnected.");
        } catch (MqttException e) {
            log.error("Cannot disconnect the broker {}: {}", brokerUri, e.getMessage(), e);
            return;
        }
        log.info("Mosquitto Showcase completed");
    }
}