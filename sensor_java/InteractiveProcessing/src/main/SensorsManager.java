//package main;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ThreadLocalRandom;

public class SensorManager implements MqttCallback {

    public SensorManager(
            final MqttAsyncCient asyncCient,
            final String boardCommandsTopicm
            final String boardDataBaseTopic,
            final String encoding) {

    }

    public IMqttDeliveryToken publishMessage(
            final String topic,
            final String textForMessage,
            IMqttActionListener actionListener,
            final int qos,
            final boolean retained) {

    }

    public void publishProcessedCommandMessage(
            final String sensorName,
            final String command) {

    }

    @Override
    public void connectionLost(Throwable cause) {
        cause.printStackTrace();
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    } //public void messageArrived end


    public void loop() {

    } //public void loop() end

} // public class SensorManager implements MqttCallback end