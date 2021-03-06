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

  - `payloadString = ... -`:  
  In case the contents of `payloadString` are not JSON, a `ValueError` exeption will be captured; the code will print a message indicating that the message doesn't include a valid command and no more code will be executed in the static method. In case the contents of `payloadString` are JSON, we will have a dictionary in the `msgDictionary` local variable.
  - `active_instance`:  
  The code uses the `active_instance` class attribute that has a reference to the active `DroneCommandProcessor` instance to call the necessary methods for either `drone`
  - After the code; `is_command_processed`:  
  Once the command has been successfully processed, the code sets the `is_command_processed` flag to `True`. Finally, the code checks the value of this falg, and if it is equal to `True`, the code cals the `publishResponseMessage` for the `DroneCommandProcessor` instance saved in the `active_instance` class attribute.

* `publishResponseMessage`
This method receives the message dictionary that has been received with the command in the `message` argument. The method calls the `json.dumps` funtion to serialize a dictionary to a JSON formatted string with the response message that indicates that the command has been successfully processed.

* `processedcommands`
This method calls the `loop` method for the MQTT client and ensure that communication with MQTT server is carried out. The drone commnad processor will receive messages and process commands.  
Details: http://www.steves-internet-guide.com/loop-python-mqtt-client/


### About the `__main__` block
This method creates an instance of the `Drone` class named `drone` with "`drone01`" as the value for the `name` argument. The next line creates an instance of the `DroneCommandProcessor` class named `droneCommandProcessor` with "`drone01`" and the previously created `Drone` instance, `drone` as the values for the `name` and  `drone` arguments. This way, `droneCommandProcessor` will delegate the execution of the commands to the instance methods in `drone`.

  The instance `droneCommandProcessor` will subscribe to the `commands/drone01` topic in the MQTT server, and therefore, we must publish messages to this topic in order to send the commands that the code will process. Additionally, this instance will publish a message to the `processedcommands/drone01` topic. Whenever a command is successfully processed, new messages will be published to the `processedcommands/drone01` topic.

  The `while` loop calls the `droneCommandProcessor.processedcommands()` method and sleeps for one second. The `processedcommands` method calls the `loop` method for the MQTT client to ensure that communication with the MQTT server is carried out.

### Test result
Leave the command running.

```
ubuntu@ip-172-31-27-200:~/certs$ mosquitto_sub -V mqttv311 -p 8883 --cafile /etc/mosquitto/ca_certificates/ca.crt -t processedcommands/drone01 -d -h 127.0.0.1 --cert device001.crt --key device001.key
Client mosqsub|16451-ip-172-31 sending CONNECT
Client mosqsub|16451-ip-172-31 received CONNACK
Client mosqsub|16451-ip-172-31 sending SUBSCRIBE (Mid: 1, Topic: processedcommands/drone01, QoS: 0)
Client mosqsub|16451-ip-172-31 received SUBACK
Subscribed (mid: 1): 0
```

#### Sending right command case
1. Execute subscribed command.
```
ubuntu@ip-172-31-27-200:~/mqttTraining/drone_python$ python drone.py
LOG: Sending CONNECT (u0, p0, wr0, wq0, wf0, c1, k60) client_id=
LOG: Received CONNACK (0, 0)
Connected to the MQTT server
LOG: Sending SUBSCRIBE (d0) [('commands/drone01', 2)]
LOG: Sending PUBLISH (d0, q0, r0, m2), 'commands/drone01', ... (32 bytes)
LOG: Received SUBACK
LOG: Received PUBLISH (d0, q0, r0, m0), 'commands/drone01', ...  (32 bytes)
I've received the following msg: drone01 is listening to messages
```

2. Publish the message to the topic; `commands/drone01`.

```
ubuntu@ip-172-31-27-200:~/certs$ mosquitto_pub -V mqttv311 -p 8883 --cafile /etc/mosquitto/ca_certificates/ca.crt -t commands/drone01 -d -h 127.0.0.1 --cert device001.crt --key device001.key -m '{"COMMAND" : "LAND"}' -q 1
Client mosqpub|16436-ip-172-31 sending CONNECT
Client mosqpub|16436-ip-172-31 received CONNACK
Client mosqpub|16436-ip-172-31 sending PUBLISH (d0, q1, r0, m1, 'commands/drone01', ... (20 bytes))
Client mosqpub|16436-ip-172-31 received PUBACK (Mid: 1)
Client mosqpub|16436-ip-172-31 sending DISCONNECT
```

3. `drone01` subscribes and processes the commands, and publish the result to the topic; `processedcommands/drone01`.

```
LOG: Received PUBLISH (d0, q1, r0, m5), 'commands/drone01', ...  (20 bytes)
LOG: Sending PUBACK (Mid: 5)
I've received the following msg: {"COMMAND" : "LAND"}
drone01: Landing
LOG: Sending PUBLISH (d0, q0, r0, m7), 'processedcommands/drone01', ... (42 bytes)
```

4. It displays all the messages received in the `processedcommands/drone01` topic.

```
Client mosqsub|16451-ip-172-31 received PUBLISH (d0, q0, r0, m0, 'processedcommands/drone01', ... (42 bytes))
{"SUCCESSFULLY_PROCESSED_COMMAND": "LAND"}
```

#### Sending wrong command case
1. Publish the *WRONG* message to the topic; `commands/drone01`
```
ubuntu@ip-172-31-27-200:~/certs$ mosquitto_pub -V mqttv311 -p 8883 --cafile /etc/mosquitto/ca_certificates/ca.crt -t commands/drone01 -d -h 127.0.0.1 --cert device001.crt --key device001.key -m 'Hello' -q 1
Client mosqpub|16438-ip-172-31 sending CONNECT
Client mosqpub|16438-ip-172-31 received CONNACK
Client mosqpub|16438-ip-172-31 sending PUBLISH (d0, q1, r0, m1, 'commands/drone01', ... (5 bytes))
Client mosqpub|16438-ip-172-31 received PUBACK (Mid: 1)
Client mosqpub|16438-ip-172-31 sending DISCONNECT
```

2. Drone01 does not receive the command and execute `exception` block.

```
LOG: Received PUBLISH (d0, q1, r0, m6), 'commands/drone01', ...  (5 bytes)
LOG: Sending PUBACK (Mid: 6)
I've received the following msg: Hello
No including a valid command
```

3. No messages received in the `processedcommands/drone01` topic.

## `iotBoardClient.py`
### 1. `LoopControl` class
* `is_last_command_processed`
The class attribute is initialized to `False`. This will be used as a flag to control the network loop.

### 2.`onConnect` function
This function is the callback that will be executed once a successful connection has been established with the MQTT server. This code calls the `subscribe` method for the MQTT client received in the `client` to subscribe to the `processed_commands_topic` with a `QoS = 0`.

### 3.`onMessage` function
This will be executed every time a new message arrives to the topic to which we have subscribed. If the message in the payload includes the `CMD_LAND_IN_SAFE_PLACE` constant, we assume that the last command was successfully executed and the code sets the `LoopControl.is_last_command_processed` to `True`

### 4.`publishCommand` function
This function receives following contents;
  - The MQTT client
  - The command name
  - The key
  - The value (ex: set to 90 when command name: CMD_ROTATE_LEFT & key: KEY_DEGREES)

This function calls the publish method to publish the `command_message` JSON formatted wtring to the topic name saved in the `commands_topic` variable with a `QoS = 2`.

### 5. About main block
The `while` loop calls the `client.loop` method to ensure that communication with the MQTT server is carried out, and then it sleeps for one second. After the last command is processed, the `LoopControl.is_last_command_processed` class variable is set to `False` and the while loop ends.

### Test result
#### 1. Drone side
Subscribe specific topic

```
ubuntu@ip-172-31-24-234:~/mqttTraining/drone_python$ python drone.py
LOG: Sending CONNECT (u0, p0, wr0, wq0, wf0, c1, k60) client_id=
LOG: Received CONNACK (0, 0)
Connected to the MQTT server
LOG: Sending SUBSCRIBE (d0) [('commands/drone01', 2)]
LOG: Sending PUBLISH (d0, q0, r0, m2), 'commands/drone01', ... (32 bytes)
LOG: Received SUBACK
LOG: Received PUBLISH (d0, q0, r0, m0), 'commands/drone01', ...  (32 bytes)
I've received the following msg: drone01 is listening to messages
```

#### 2. iotBoardClient side
Publish commands & receive necessary packets

```
ubuntu@ip-172-31-24-234:~/mqttTraining/drone_python$ python iotBoardClient.py
Log: Sending CONNECT (u0, p0, wr0, wq0, wf0, c1, k60) client_id=
Log: Sending PUBLISH (d0, q2, r0, m1), 'commands/drone01', ... (23 bytes)
Log: Sending PUBLISH (d0, q2, r0, m2), 'commands/drone01', ... (22 bytes)
Log: Sending PUBLISH (d0, q2, r0, m3), 'commands/drone01', ... (24 bytes)
Log: Sending PUBLISH (d0, q2, r0, m4), 'commands/drone01', ... (41 bytes)
Log: Sending PUBLISH (d0, q2, r0, m5), 'commands/drone01', ... (42 bytes)
Log: Sending PUBLISH (d0, q2, r0, m6), 'commands/drone01', ... (33 bytes)
Log: Received CONNACK (0, 0)
Connect result: Connection Accepted.
Log: Sending SUBSCRIBE (d0) [('processedcommands/drone01', 0)]
Log: Received PUBREC (Mid: 1)
Log: Sending PUBREL (Mid: 1)
Log: Received PUBREC (Mid: 2)
Log: Sending PUBREL (Mid: 2)
Log: Received PUBREC (Mid: 3)
Log: Sending PUBREL (Mid: 3)
Log: Received PUBREC (Mid: 4)
Log: Sending PUBREL (Mid: 4)
Log: Received PUBREC (Mid: 5)
Log: Sending PUBREL (Mid: 5)
Log: Received PUBREC (Mid: 6)
Log: Sending PUBREL (Mid: 6)
Log: Received SUBACK
Subscribed with QoS: 0, Mid: 7
Log: Received PUBCOMP (Mid: 1)
Log: Received PUBCOMP (Mid: 2)
Log: Received PUBCOMP (Mid: 3)
Log: Received PUBCOMP (Mid: 4)
Log: Received PUBCOMP (Mid: 5)
Log: Received PUBLISH (d0, q0, r0, m0), 'processedcommands/drone01', ...  (46 bytes)
{"SUCCESSFULLY_PROCESSED_COMMAND": "TAKE_OFF"}
Log: Received PUBLISH (d0, q0, r0, m0), 'processedcommands/drone01', ...  (45 bytes)
{"SUCCESSFULLY_PROCESSED_COMMAND": "MOVE_UP"}
Log: Received PUBLISH (d0, q0, r0, m0), 'processedcommands/drone01', ...  (47 bytes)
{"SUCCESSFULLY_PROCESSED_COMMAND": "MOVE_BACK"}
Log: Received PUBLISH (d0, q0, r0, m0), 'processedcommands/drone01', ...  (49 bytes)
{"SUCCESSFULLY_PROCESSED_COMMAND": "ROTATE_LEFT"}
Log: Received PUBLISH (d0, q0, r0, m0), 'processedcommands/drone01', ...  (50 bytes)
{"SUCCESSFULLY_PROCESSED_COMMAND": "ROTATE_RIGHT"}
Log: Received PUBCOMP (Mid: 6)
Log: Received PUBLISH (d0, q0, r0, m0), 'processedcommands/drone01', ...  (56 bytes)
{"SUCCESSFULLY_PROCESSED_COMMAND": "LAND_IN_SAFE_PLACE"}
Log: Sending DISCONNECT
```

#### 3. Drone side
Drone sends necessary packets and operate commands 

```
LOG: Received PUBLISH (d0, q2, r0, m1), 'commands/drone01', ...  (23 bytes)
LOG: Sending PUBREC (Mid: 1)
LOG: Received PUBLISH (d0, q2, r0, m2), 'commands/drone01', ...  (22 bytes)
LOG: Sending PUBREC (Mid: 2)
LOG: Received PUBLISH (d0, q2, r0, m3), 'commands/drone01', ...  (24 bytes)
LOG: Sending PUBREC (Mid: 3)
LOG: Received PUBREL (Mid: 1)
I've received the following msg: {"COMMAND": "TAKE_OFF"}
drone01: Taking off
LOG: Sending PUBLISH (d0, q0, r0, m3), 'processedcommands/drone01', ... (46 bytes)
LOG: Sending PUBCOMP (Mid: 1)
LOG: Received PUBLISH (d0, q2, r0, m4), 'commands/drone01', ...  (41 bytes)
LOG: Sending PUBREC (Mid: 4)
LOG: Received PUBLISH (d0, q2, r0, m5), 'commands/drone01', ...  (42 bytes)
LOG: Sending PUBREC (Mid: 5)
LOG: Received PUBREL (Mid: 2)
I've received the following msg: {"COMMAND": "MOVE_UP"}
drone01: Moving up
LOG: Sending PUBLISH (d0, q0, r0, m4), 'processedcommands/drone01', ... (45 bytes)
LOG: Sending PUBCOMP (Mid: 2)
LOG: Received PUBREL (Mid: 3)
I've received the following msg: {"COMMAND": "MOVE_BACK"}
drone01: Moving back
LOG: Sending PUBLISH (d0, q0, r0, m5), 'processedcommands/drone01', ... (47 bytes)
LOG: Sending PUBCOMP (Mid: 3)
LOG: Received PUBREL (Mid: 4)
I've received the following msg: {"DEGREES": 90, "COMMAND": "ROTATE_LEFT"}
drone01: Rotating left 90 degrees
LOG: Sending PUBLISH (d0, q0, r0, m6), 'processedcommands/drone01', ... (49 bytes)
LOG: Sending PUBCOMP (Mid: 4)
LOG: Received PUBREL (Mid: 5)
I've received the following msg: {"DEGREES": 45, "COMMAND": "ROTATE_RIGHT"}
drone01: Rotating right 45 degrees
LOG: Sending PUBLISH (d0, q0, r0, m7), 'processedcommands/drone01', ... (50 bytes)
LOG: Sending PUBCOMP (Mid: 5)
LOG: Received PUBLISH (d0, q2, r0, m6), 'commands/drone01', ...  (33 bytes)
LOG: Sending PUBREC (Mid: 6)
LOG: Received PUBREL (Mid: 6)
I've received the following msg: {"COMMAND": "LAND_IN_SAFE_PLACE"}
drone01: Landing in a safe place
LOG: Sending PUBLISH (d0, q0, r0, m8), 'processedcommands/drone01', ... (56 bytes)
LOG: Sending PUBCOMP (Mid: 6)
```
