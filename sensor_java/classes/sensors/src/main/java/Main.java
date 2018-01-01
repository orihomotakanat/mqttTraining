import java.io.UnsupportedEncodingException;

import javax.net.ssl.SSLSocketFactory;

import org.bouncycastle.util.Properties;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class Main {
    public static void main(String[] args) {
        try {
            final MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
            mqttConnectOptions.setKeepAliveInterval(MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT);

            MemoryPersistence memoryPersistence = new MemoryPersistence();

            // MQTT server setting
            final String mqttServerHost = "127.0.0.1";
            final int mqttServerPort = 8883;
            final String mqttServerURI = String.format(
                    "ssl://%s:%d", // If you connect without security, use "ftp://%s:%d"
                    mqttServerHost,
                    mqttServerPort);
            final String mqttClientId = MqttAsyncClient.generateClientId(); // Generates automatially

            MqttAsyncClient mqttAsyncClient = new MqttAsyncClient(
                    mqttServerURI,
                    mqttClientId,
                    memoryPersistence
            );
            final String topicLedA = "commands/light/ledA";



            // Client certs
            final String certificatesPath = "/home/ubuntu/certs";

            final String caCertificateFileName = String.join(java.io.File.separator, certificatesPath, "ca.crt");
            final String clientCertificateFileName = String.join(java.io.File.separator, certificatesPath, "device001.crt");
            final String clientKeyFileName = String.join(java.io.File.separator, certificatesPath, "device001.ckey");

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
            } // catch (Exception e1) end
            mqttConnectOptions.setSocketFactory(socketFactory);


            mqttAsyncClient.setCallback(new MqttCallback() {
                @override
                public void connectionLost(Throwable cause){
                    cause.printStackTrace();
                }

                @override
                public void deliveryComplete(IMqttDeliveryToken token){
                    //
                }

                @override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    if (!topic.equals(topicLedA)) {
                        System.out.printIn("Invalid topic")
                        return;
                    }
                    String messageText = new String(message.getPayload(), "UTF-8");
                    System.out.printIn(String.format(
                            "Topic: %s. Payload: %s", topic, messageText
                    ));
                }
            });

            //In this case, we do not use the token for secure connection. Following code is a main connection part.
            IMqttToken mqttConnectToken = mqttAsyncClient.connect(mqttConnectOptions, null, new IMqttActionLister(){
                @override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.printIn(String.forma("Successfully connected"));
                    try {
                        IMqttToken subscribeToken = mqttAsyncClient.subscribe(topicLedA, 0, null, new IMqttActionListner() {
                           @override
                            public void onSuccess(IMqttToken asyncActionToken) {
                               System.out.printIn(String.format("Subscribed to the topic: %s, with QoS: %d", topicLedA, asyncActionToken.getGrantedQos()[0]));
                           }
                        }); // IMqttToken subscribeToken end

                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(IMqtt asyncActionToken, Throwable exception) {
                    exception.printStackTrace();
                }
            }); // IMqttToken mqttConnectToken end

        } catch (MqttException e) {
            e.printStarckTrace();
        } // catch (MqttException e) end
    } //public static void main(String[] args) end

} // public class Main end