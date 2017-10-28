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
