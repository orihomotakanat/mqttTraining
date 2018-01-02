//package main;

import java.io.UnsupportedEncodingException;

import javax.net.ssl.SSLSocketFactory;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

public class Main {
    private final static String BOARD_NAME = "LocationA";
    private final static String ENCODING_FOR_PAYLOAD = "UTF-8";

    public static void main(String[] args) {
        final String boardCommandsTopic = String.format("commands/boards/%s", BOARD_NAME);
        final String boardDataBaseTopic = String.format("data/boards/%s/", BOARD_NAME);
        final String boardStatusTopic = String.format("status/boards/%s", BOARD_NAME);
        final String commandsTopicFilter = String.format("%s/+", boardCommandsTopic);

        try {
            final MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
            mqttConnectOptions.setKeepAliveInterval(MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT);

            final String lastWillMessageText = "OFFLINE";
            byte[] bytesForPayload;
            try {
                bytesForPayload = lastWillMessageText.getBytes(ENCODING_FOR_PAYLOAD);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();;
                return;
            }
            mqttConnectOptions.setWill(boardStatusTopic, bytesForPayload, 2, true);

            MqttDefaultFilePersistence filePersistence = new MqttDefaultFilePersistence();

            final String mqttServerHost = "127.0.0.1"; //Internal
            final int mqttServerPort = 8883;
            final String mqttServerURI = String.format(
                    "ssl://%s:%d",
                    mqttServerHost,
                    mqttServerPort);

            final String mqttClientId = MqttAsyncClient.generateClientId();
            MqttAsyncClient mqttAsyncClient = new MqttAsyncClient(
                    mqttServerURI,
                    mqttClientId,
                    filePersistence);

            // Client certs
            final String certificatesPath = "/home/ubuntu/certs";

            final String caCertificateFileName = String.join(java.io.File.separator, certificatesPath, "ca.crt");
            final String clientCertificateFileName = String.join(java.io.File.separator, certificatesPath, "device001.crt");
            final String clientKeyFileName = String.join(java.io.File.separator, certificatesPath, "device001.key");

            // Create socketFactory, if you connect without security, you do not have to call the this part.
            SSLSocketFactory socketFactory;
            try {
                socketFactory = SecurityHelper.createSocketFactory(
                        caCertificateFileName,
                        clientCertificateFileName,
                        clientKeyFileName
                );
            } catch (Exception e1) {
                e1.printStackTrace();
                return;
            }

            mqttConnectOptions.setSocketFactory(socketFactory);
            SensorsManager sensorsManager = new SensorsManager(mqttAsyncClient, boardCommandsTopic, boardDataBaseTopic, ENCODING_FOR_PAYLOAD);
            mqttAsyncClient.setCallback(sensorsManager);

            IMqttToken mqttConnectToken = mqttAsyncClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            System.out.println(String.format("Successfully connected"));
                            try {
                                IMqttToken subscribeToken = mqttAsyncClient.subscribe(commandsTopicFilter, 2, null, new IMqttActionListener() {
                                    @Override
                                    public void onSuccess(IMqttToken asyncActionToken) {
                                        System.out.println(
                                                String.format(
                                                        "Subscribed to the %s topic with QoS: %d",
                                                        asyncActionToken.getTopics()[0],
                                                        asyncActionToken.getGrantedQos()[0]));
                                        sensorsManager.publishMessage(boardStatusTopic, "ONLINE", null, 2, true);
                                    }

                                    @Override
                                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                        exception.printStackTrace();
                                    }
                                });
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) { //// This method is fired when an operation failed
                            exception.printStackTrace();
                        }
                    }

            );

            while (true) {
                sensorsManager.loop();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.err.format("Sleep interruption: %s", e.toString());
                }
            } //while (true) end

        } catch (MqttException e) {
            e.printStackTrace();
        } //try end

    } //public static void main end

} //public class Main end