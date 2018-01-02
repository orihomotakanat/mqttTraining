package main;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ThreadLocalRandom;

public class SensorsManager implements MqttCallback {

    //Commands
    private static final String SENSOR_HUMIDITY = "humidity";
    private static final String SENSOR_LIGHT_VALUE = "lightvalue";
    private static final String TOPIC_SEPARATOR = "/";
    private final String boardCommandsTopic;
    private final String boardDataBaseTopic;
    private final String encoding;
    private final MqttAsyncClient asyncClient;
    private final String humidityTopic;
    private final String visibleLightTopic;
    private final String infraredLightTopic;
    private final String ultraVioletIndexTopic;
    private volatile boolean isLightValueSensorTurnedOn = false;
    private volatile boolean isHumiditySensorTurnedOn = false;

    public SensorsManager(
            final MqttAsyncClient asyncClient,
            final String boardCommandsTopic,
            final String boardDataBaseTopic,
            final String encoding) {
        this.boardCommandsTopic = boardCommandsTopic;
        this.boardDataBaseTopic = boardDataBaseTopic;
        this.encoding = encoding;
        this.asyncClient = asyncClient;
        this.humidityTopic = this.boardDataBaseTopic.concat(SENSOR_HUMIDITY);

        final String lightValueTopic = boardDataBaseTopic.concat(SENSOR_LIGHT_VALUE);

        this.visibleLightTopic = String.join(TOPIC_SEPARATOR, lightValueTopic, "visibleLight");
        this.infraredLightTopic = String.join(TOPIC_SEPARATOR, lightValueTopic, "ir");
        this.ultraVioletIndexTopic = String.join(TOPIC_SEPARATOR, lightValueTopic, "uv");
    }

    public IMqttDeliveryToken publishMessage(
            final String topic,
            final String textForMessage,
            IMqttActionListener actionListener,
            final int qos,
            final boolean retained) {
        byte[] bytesForPayload;
        try {
            bytesForPayload = textForMessage.getBytes(this.encoding);
            return asyncClient.publish(topic, bytesForPayload, qos,
                    retained, null, actionListener);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (MqttException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void publishProcessedCommandMessage(
            final String sensorName,
            final String command) {
        final String topic = String.format("%s/%s", boardCommandsTopic, sensorName);
        final String textForMessage = String.format("%s successfully processsed command: %s", sensorName, command);
        publishMessage(topic, textForMessage, null, 0, false);
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
        String messageText = new String(message.getPayload(), encoding);
        System.out.println(String.format("Topic: %s, Payload: %s", topic, messageText));

        if(!topic.startsWith(boardCommandsTopic)) {
            //The topic for the arrived message doesn't start with boardTopic
            System.out.println("Cannot start: Invalid topic");
            return;
        }

        final boolean isTurnOnMessage = messageText.equals("TURN ON");
        final boolean isTurnOffMessage = messageText.equals("TURN OFF");
        boolean isInvalidCommand = false;
        boolean isInvalidTopic = false;

        //Extract the sensor name from the topic
        String sensorName = topic.replaceFirst(boardCommandsTopic, "").replaceFirst(TOPIC_SEPARATOR, "");
        switch (sensorName) {
            case SENSOR_LIGHT_VALUE:
                if (isTurnOnMessage) {
                    isLightValueSensorTurnedOn = true;
                } else if (isTurnOffMessage) {
                    isLightValueSensorTurnedOn = false;
                } else {
                    isInvalidCommand = true;
                }
                break;
            case SENSOR_HUMIDITY:
                if (isTurnOnMessage) {
                    isHumiditySensorTurnedOn = true;
                } else if (isTurnOffMessage) {
                    isHumiditySensorTurnedOn = false;
                } else {
                    isInvalidCommand = true;
                }
                break;
            default:
                isInvalidCommand = true;
        } //switch (sensorName) end
        if (!isInvalidCommand && !isInvalidTopic) {
            publishProcessedCommandMessage(sensorName, messageText);
        }

    } //public void messageArrived end


    public void loop() {
        if (isHumiditySensorTurnedOn) {
            final int humidityLevel = ThreadLocalRandom.current().nextInt(1, 101); //Retrieve the humidity level from the sensor (In this case, just generating number)
            publishMessage(humidityTopic, String.format("%d %%", humidityLevel), null, 0, false);
        }

        if (isLightValueSensorTurnedOn) {
            final int visibleLight = ThreadLocalRandom.current().nextInt(201, 301);
            publishMessage(visibleLightTopic, String.format("%d lm", visibleLight), null, 0, false);

            final int infraredLight = ThreadLocalRandom.current().nextInt(251,281);
            publishMessage(infraredLightTopic, String.format("%d lm", infraredLight), null, 0, false);

            final int ultraVioletIndex = ThreadLocalRandom.current().nextInt(0, 16);
            publishMessage(ultraVioletIndexTopic, String.format("%d UV Index", ultraVioletIndex), null, 0, false);
        }
    } //public void loop() end

} // public class SensorsManager implements MqttCallback end