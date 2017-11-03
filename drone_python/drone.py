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
        DroneCommandProcessor.on_log = on_log
        self.client.on_connect = DroneCommandProcessor.onConnect
        self.client.on_message = DroneCommandProcessor.onMessage
        self.client.tls_set(ca_certs=ca,
            certfile=clientCert,
            keyfile=clientKey)
        self.client.connect(host=mqttServerHost,
            port=mqttPort,
            keepalive=mqttKeepAlive)

    @staticmethod
    def onConnect(client, userdata, flags, rc):
        print("Connected to the MQTT server")
        client.subscribe(DroneCommandProcessor.commands_topic, qos=2)
        client.publish(topic=DroneCommandProcessor.commands_topic, payload="{} is listening to messages".format(DroneCommandProcessor.active_instance.name))

    @staticmethod
    def onMessage(client, userdata, msg):
        payloadString = msg.payload.decode('utf-8')
        if msg.topic == DroneCommandProcessor.commands_topic:
            print("I've received the following msg: {0}".format(payloadString))
        try:
            msgDictionary = json.load(payloadString)
            if COMMAND_KEY in msgDictionary:
                command = msgDictionary[COMMAND_KEY]
                drone = DroneCommandProcessor.active_instance.drone
                is_command_processed = False
                if command == CMD_TAKE_OFF:
                    drone.take_off()
                    is_command_processed = True
                elif command == CMD_LAND:
                    drone.land()
                    is_command_processed = True
                elif command == CMD_LAND_IN_SAFE_PLACE:
                    drone.land_in_safe_place()
                    is_command_processed = True
                elif command == CMD_MOVE_UP:
                    drone.move_up()
                    is_command_processed = True
                elif command == CMD_MOVE_DOWN:
                    drone.move_down()
                    is_command_processed = True
                elif command == CMD_MOVE_FORWARD:
                    drone.move_forward()
                    is_command_processed = True
                elif command == CMD_MOVE_BACK:
                    drone.move_back()
                    is_command_processed = True
                elif command == CMD_MOVE_LEFT:
                    drone.move_left()
                    is_command_processed = True
                elif command == CMD_MOVE_RIGHT:
                    drone.move_right()
                    is_command_processed = True
                elif command == CMD_ROTATE_RIGHT:
                    degrees = msgDictionary[KEY_DEGREES]
                    drone.rotate_right(degrees)
                    is_command_processed = True
                elif command == CMD_ROTATE_LEFT:
                    degrees = msgDictionary[KEY_DEGREES]
                    drone.rotate_left(degrees)
                    is_command_processed = True
                elif command == CMD_SET_MAX_ALTITUDE:
                    feet = msgDictionary[KEY_FEET]
                    drone.set_max_altitude(feet)
                    is_command_processed = True
                elif command == CMD_SET_MIN_ALTITUDE:
                    feet = msgDictionary[KEY_FEET]
                    drone.set_min_altitude(feet)
                    is_command_processed = True
                if is_command_processed:
                    DroneCommandProcessor.active_instance.publishResponseMessage(
                            msgDictionary)
                else:
                    print("The message includes an unknown command.")
        except ValueError: # Msg is not a dictionary or no json object could be decoded
            print("No including a valid command")

    def publishResponseMessage(self, message):
        responseMessage = json.dumps({
            SUCCESSFULLY_PROCESSED_COMMAND_KEY: message[COMMAND_KEY]})
        result = self.client.publish(
            topic = self.__class__.processed_commands_topic,
            payload = responseMessage)
        return result

    def processCommands(self):
        self.client.loop()
        # Like calling to the loop method as synchronizing your mailbox.
        # Any pending messages to be published in the outgoing bo will be sent and any incoming mesages will arrive to the inbox
        # and events that we have previously analyzes will be fired.
        # See README.md.

    # Debug
    def onLog(client, userdata, level, buf):
        print("LOG: {}".format(buf))

if __name__ == "__main__":
    drone = Drone("drone01")
    droneCommandProcessor = DroneCommandProcessor("drone01", drone)
    while True:
        droneCommandProcessor.processCommands() # Process msgs and the commands every 1 second
    time.sleep(1)
