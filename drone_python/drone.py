from command import *
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


# Drone class
class Drone:
    def __init__(self, name):
        self.name = name
        self.min_altitude = 0
        self.max_altitude = 30

    def print_with_name_prefix(self, message): # For debug
        print("{}: {}".format(self.name, message))

    def take_off(self):
        self.print_with_name_prefix("Taking off")

    def land(self):
        self.print_with_name_prefix("Landing")

    def land_in_safe_place(self):
        self.print_with_name_prefix("Landing in a safe place")

    def move_up(self):
        self.print_with_name_prefix("Moving up")

    def move_down(self):
        self.print_with_name_prefix("Moving down")

    def move_forward(self):
        self.print_with_name_prefix("Moving forward")

    def move_back(self):
        self.print_with_name_prefix("Moving back")

    def move_left(self):
        self.print_with_name_prefix("Moving left")

    def move_right(self):
        self.print_with_name_prefix("Moving right")

    def rotate_right(self, degrees):
        self.print_with_name_prefix("Rotating right {} degrees".format(degrees))

    def rotate_left(self, degrees):
        self.print_with_name_prefix("Rotating left {} degrees".format(degrees))

    def set_max_altitude(self, feet):
        self.max_altitude = feet
        self.print_with_name_prefix("Setting maximum altitude to {} feet".format(feet))

    def set_min_altitude(self, feet):
        self.min_altitude = feet
        self.print_with_name_prefix("Setting minimum altitude to {} feet".format(feet))

# DroneCommandProcessor class
class DroneCommandProcessor:
    commands_topic = ""
    processed_commands_topic = ""
    active_instance = None

    def __init__(self, name, drone):
        self.name = name
        self.drone = drone
        DroneCommandProcessor.commands_topic = "commands/{}".format(self.name)
        DroneCommandProcessor.processed_commands_topic = "processedcommands/{}".format(self.name)
        self.client = mqtt.Client(protocol=mqtt.MQTTv311)
        DroneCommandProcessor.active_instance = self
        self.client.on_connect = DroneCommandProcessor.onConnect
        self.client.on_message = DroneCommandProcessor.onMessage
        self.client.tls_set(ca_certs=ca,
            certfile=clientCert
            keyfile=clientKey)
        self.client.connect(host=mqttServerHost,
            port=mqttPort,
            keepalive=mqttKeepAlive)

    @staticmethod
    def onConnect(client, userdata, flags, rc):
        print("Connected to the MQTT server")
        client.subscribe(DroneCommandProcessor.commands_topic, qos=2)
        client.publish(topic=DroneCommandProcessor.commands_topic, payload="{} is listening to messages".format(DroneCommandProcessor.active_instance.name)

    def onMessage(client, userdata, msg)

    # Debug
    def onLog(client, userdata, level, buf):
        print("LOG: {}".format(buf))
