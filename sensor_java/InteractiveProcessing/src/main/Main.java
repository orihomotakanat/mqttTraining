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

        try {

        } catch (MqttException e) {
            e.printStackTrace();
        } //try end

    } //public static void main end

} //public class Main end