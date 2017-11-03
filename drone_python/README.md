# A example of controlling a drone with Python code
## `subscribeClient.py`
### About callback functions
* `onConnect`
This function will be called when the MQTT client receives a `CONNACK` response from MQTT server, that is, when a connection has been successfully established with the MQTT server,

* `onSubscribe`
This function will be called when the MQTT client receives a `SUBACK` response from MQTT server, that is, when a subsciption has been successfully completed.

* `onMessage`
This function will be called when the MQTT client receives a `PUBLISH` message from MQTT server. Based on the subscriptions for the client, whenever the MQTT server publishes a message, this function will be called.

### About the main block
1. The main block of this code creates an instance of the `mqtt.Clinet` class (`paho.mqtt.clinet.Client`) that represents an MQTT client and that we will use to communicate with a MQTT broker. We specified `mqtt.MQTTv311` as the value for the `protocol` argument (the default: MQTT version 3.1).
2. The code assigns the `onConnect` function to the `client.on_connect` attribute. Similarly, the `onSubscribe` and `onMessage` functions assign to each attribute.
3. The call to the `client.tls_set` method is very important before running the `client.connect` method.
4. Finally, the main block calls the `client.connect_async` method and specifies the values for the `host`, `port` and `keepAlive` arguments.This way, the code
asks the MQTT client to established a connection to the specified MQTT server.
* The `connect_async` method runs with an asynchoronous execution, and therefore, it si a non-blocking call.
* The equivalent `connect` method runs with a synchoronous exection.

5. After a connection has been successfully established with the MQTT server, the specified callback in the `client.on_connect` attribute will be executed, that is, the `onConnect` function. This function recives the `mqtt.Client` instance that established the connection with the MQTT server in the `client` argument.
6. The main block calls the `client.loop_forever` method that calls the loop method for us in an infinite blocking loop. We will receive the messages whose topic matches the topic to which we have subscribed.

### Test result
1. (python client side) Start subscription
```
~/mqttTraining/drone_python$ python subscribeClient.py
Log: Sending CONNECT (u0, p0, wr0, wq0, wf0, c1, k60) client_id=
Log: Received CONNACK (0, 0)
Connection status: Connection Accepted.
Log: Sending SUBSCRIBE (d0) [('test/drone01', 0)]
Log: Received SUBACK
Subscribed with QoS: 0
```

2. (Another client side) Publish message
```
~/certs$ mosquitto_pub -V mqttv311 -p 8883 --cafile /etc/mosquitto/ca_certificates/ca.crt -t test/drone01 -d -h 127.0.0.1 --cert device001.crt --key device001.key -m '{"COMMAND" : "LAND"}' -q 1
Client mosqpub|1910-ip-172-31- sending CONNECT
Client mosqpub|1910-ip-172-31- received CONNACK
Client mosqpub|1910-ip-172-31- sending PUBLISH (d0, q1, r0, m1, 'test/drone01', ... (20 bytes))
Client mosqpub|1910-ip-172-31- received PUBACK (Mid: 1)
Client mosqpub|1910-ip-172-31- sending DISCONNECT
```

3. (python client side) Subscribe message
```
Log: Received PUBLISH (d0, q0, r0, m0), 'test/drone01', ...  (20 bytes)
Topic: test/drone01. Payload: {"COMMAND" : "LAND"}
```

## `drone.py`
### About `command.py`
This contents declare many variables with the values that identity each of the supported commnads for the drone. Additionally, the code declares specification of successfully proccessed command (Key strings). All these variables are defined with all letters uppercase beacuse we will use them as constants.

### About two classes
1. `Drone` : A class to represent a drone
This class will represent a drone and provides methods that will be called whenever a command has to be processed.

2. `DroneCommandProcessor` : Receiving messages
This class will represent a command processor that will establish a connection with an MQTT server, subscribe to a topic in which tha MQTT client will receive messages with commands, analyze the incomming messages, and delegate the exection of the commands to an associated instance of the `Drone` class. This class will declare many static methods that we will specify as the callbacks for the MQTT client.

#### 1. `Drone` class
The `__init__` method saves the received `name` in an attribute with the same name. Then, this method sets the initial values for `min_altitude` and `max_altitude`

#### 2. `DroneCommandProcessor` class
* `__init__` method
This method saves receive `name` and `drone` in attributes with the same names. Then, this method sets the values for the `commands_topic` and `processed_commands_topic` class attribute by `DroneCommandProcessor.commands_topic` and `roneCommandProcessor.processed_commands_topic`. The MQTT client will receive messages in the topic name saved in the `commands_topic` class attribute and will publish messages to the topic name saved in the `processed_commands_topic` class attribute.

  The code also saves a reference to `mqtt.client` instance in the `active_instance` class attribute because we have to access the instance in the static methods that this `__init__` method will specify as callbacks for the different events that the MQTT client fires. We want to have all the methods related to the drone command processor in `DroneCommandProcessor` class.

* `onConnect`
After a connection has been successfully established with the MQTT server, the specified callback in the `self.client.on_connect` attribute (`onConnect` static method) will be executed. This static method receives the **`mqtt.Client` instance** (already established the connection with the MQTT server). Finally, after subscribing and publishing, the message starts with the drone name.

* `onMessage`
Whenever there is a new message received in the topic; `commands_topic` class attribute to which we have subscribed, the specified callback in the `self.client.onMessage` attribute will be executed (the `onMessage` static method).
This static method receives the `mqtt.Client` instance that established the connection with the MQTT broker in the `client` argument and and `mqtt.MQTTMessage` instance in the `msg` argument. The `mqtt.MQTTMessage` class describes an incoming message. At first, the static method checks whether the `msg.topic` attribute, which indicates the topic in which the message has been received, matches thr value in the `commands_topic` class attribute. In this case, whenever the `onMessage` method is executed, the value in the `msg.topic` will always match the value in the `topic` class attributes. For subscription to more than one topic, we included the code whichis check the `topic` for the received message.
