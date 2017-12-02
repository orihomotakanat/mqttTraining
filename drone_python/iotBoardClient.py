from commnd import *
import paho.mqtt.client as mqtt
import os.path
import time
import json

# client setting
deviceCertificatePath = "/home/ubuntu/certs/"
ca = os.path.join(deviceCertificatePath, "ca.crt")
clientCert =  os.path.join(deviceCertificatePath, "device001.crt")
clientKey = os.path.join(deviceCertificatePath, "device001.key")
mqttServerHost = "127.0.0.1"
mqttPort = 8883
mqttKeepAlive = 60

# device setting
drone_name = "drone01"
commands_topic = "commands/{}".format(drone_name)
processed_commands_topic = "processedcommands/{}".format(drone_name)

# LoopControl class
class LoopControl:
    is_last_command_processed = False

    def onConnect(client, userdata, rc):
        print("Connect result: {}".format(mqtt.connack_string(rc)))
        client.subscribe(processed_commands_topic)

    def onMessage(client,userdata,msg):
        if msg.topic == processed_commands_topic:
            payloadString = msg.payload.decode('utf-8')
            print(payloadString)

            if payloadString.count(CMD_LAND_IN_SAFE_PLACE) > 0:
                LoopControl.is_last_command_processed = True

    def onSubscribe(client, userdata, mid, granted_qos):
        print("Subscribed with QoS: {}, Mid: {}".format(granted_qos[0], mid))

    def publishCommand(client, command_name, key="", value=""):
        if key:
            command_meesage = json.dumps({
                COMMAND_KEY: command_name, key: value})
        else:
            command_meesage = json.dumps({
                COMMAND_KEYL command_name})
        result = client.publish(topic = commands_topic, payload = command_meesage, qos = 2)
        return result

    # Debug
    def onLog(client, userdata, level, buf):
        print("LOG: {}".format(buf))
