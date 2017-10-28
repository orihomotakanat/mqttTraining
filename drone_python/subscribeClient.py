import paho.mqtt.client as mqtt
import os.path

# client setting
deviceCertificatePath = "/ubuntu/device001"
ca = os.path.join(deviceCertificatePath, "ca.crt")
clientCert =  os.path.join(deviceCertificatePath, "device001.crt")
clientKey = os.path.join()
mqttServerHost = "127.0.0.1"
mqttPort = 8883
mqttKeepAlive = 60

# Functions
def onConnect(client, userdata, rc):
    print("Connection status: {}".format(mqtt.connack_string(rc)))
    client.subscribe("test/drone01")

def onSubscribe(client, userdata, mid, grantedQos):
    print("Subscribed with QoS: {}".format(grantedQos[0]))

def onMessage(client, userdata, msg):
    payloadString = msg.payload.decode('utf-8')
    print("Topic: {}. Payload: {}".format(
        msg.topic,
        payloadString))

# Main
if __name__ == "__main__":
    client = mqtt.Client(protocol=mqtt.MQTTv311)
    client.on_connect = onConnect
    client.on_subscribe = onSubscribe
    client.on_message = onMessage
    client.tls_set(ca_certs=ca,
        certfile=clientCert,
        keyfile=clientKey)
    client.connect_async(host=mqttServerHost,
        port=mqttPort,
        keepAlive=mqttKeepAlive)
    client.loop_forever()
