# A example of home automation with Javascript code
## Preparation of home automation
1. Install apache webserver
```
$ sudo apt install apache2
Reading package lists... Done
Building dependency tree       
Reading state information... Done

$ curl -v localhost | head -1
* Rebuilt URL to: localhost/
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
  0     0    0     0    0     0      0      0 --:--:-- --:--:-- --:--:--     0*   Trying 127.0.0.1...
* Connected to localhost (127.0.0.1) port 80 (#0)
> GET / HTTP/1.1
> Host: localhost
> User-Agent: curl/7.47.0
> Accept: */*
>
< HTTP/1.1 200 OK

```

2. Install node framework
```
$ sudo apt install npm
$ npm -v
3.5.2
```

Install node via `nodenv` and write following contents to `~/.bash_profile` (or `~/.bashrc`)

```
$ git clone git://github.com/nodenv/nodenv.git ~/.nodenv

$ echo 'export PATH="$HOME/.nodenv/bin:$PATH"' >> ~/.bash_profile
$ echo 'eval "$(nodenv init -)"' >> ~/.bash_profile
```

Set the path to `~/.bashrc` for using node.

```
#nodeenv
if [ -f ~/.bash_profile ]; then
    . ~/.bash_profile
fi

$ nodenv -v
nodenv 1.1.2-1-g18489d7
```

Install plugin

```
$ git clone https://github.com/nodenv/node-build.git ~/.nodenv/plugins/node-build
```

Install latest version `node` via nodenv

```
$ nodenv install -l
Available versions:
  0.1.14
  0.1.15
  0.1.16
  0.1.17
...
  9.0.0
  9.1.0
  9.2.0
  chakracore-dev
...
  jxcore+v8-0.3.1.1
  nightly
  node-dev
  rc
  v8-canary
```

Set latest version

```
$ nodenv global 9.2.0
$ node -v
v9.2.0
```

Update npm

```
$ npm install -g npm
$ npm -v
5.6.0
```

## `automationView.html`
### Loading scripts
* `mqttws31.js`: This is the Paho js client (source: https://www.eclipse.org/paho/clients/js/).
* `jscolor.js`: This is the jscolor file (source: https://github.com/alanswx/WebPixelFrame).
* `homeAutomation.js`: This script file declares the `APP.homeAutomation.Manager` object.

### Body section
This section declares a `div` element whose `id` is `status` and will change its tet when a connection is successfully established with the MQTT server.

Each `div` that represents and LED declares an `input` element with the `jscolor` class that will display the JSColor pure js color picker. The followingline shows the code that runs for the first LED: `APP.homeAutomation.Manager.updateLed(1, this.jscolor)`

The line calls the `APP.homeAutomation.Manager.updateLed` function with the LED identifiew as an arugument(`1`) and a string with the selected color fomr the color picker (`this.jscolor`).

In addition, the `div` that represents an LED declares an SVG circle whise `id` is `ledStatusXXX`, followed by the LED identifier. Whenever IoT boards successfully updates the color for the LED and publishes a status messagem the web page will receive this message and update the fill color for the circle with the color the user has chosen.

The script element at the end of the code uses pure js to calls the `APP.homeAutomation.Manager.connect` function once the page DOM is ready for the js code to execute.


## `homeAutomation.js`
This code provides an `APP.HomeAutomation.Manager` object with many strings, objects, and functions that we will use to make it easy to establish a connection with MQTT over WS. The function works with the Paho js client.


### `APP.HomeAutomation.Manager` object
* `mqttConnectOptions`
This object specifies values for the different options that we will use to establish a connection with the MQTT server. To work with MQTTv3.1.1, it is needed to use a cleas session and work with an unsecured connection.

#### `updateLed`
This function receives the LED ID and the RGB color in the `ledID` and `jscolor` arguments. After creating a new `Paho.MQTT.Message` instance named `message` with `payloadString`, the code sets the value of the `message.distinationName` attribute to `thisledCommandBaseTopic`, concatenated with the value received in the `ledId` argument. This `destinationName` attribute for a `Paho.MQTT.Message` instance specifies the topic to which a message has to be published.

The `connect` function creates a `Paho.MQTT.Client` instance and saves it in `this.client`. Finally, the `updateLed` function calls the `this.client.send` function with the message as an argument to publish the MQTT message. The function runs with an asynchronous execution, and agter the message is successfully delivered, the `Paho.MQTT.onMessageDelivered` attribute in the `connect` function.

#### `onConnectionLost`
This function receives a `responseObject` object as an argument, and prints a message to the console log with the value of the `responseObject.errorMessage` attribute when this attribute is not equal to 0.

#### `onMessageArrived`
This function receives a `Paho.MQTT.Message` instance in the `message` arugument. After printing a message to the console log about the detination topic (`message.destinationName`) and the payload string (`message.payloadString`) attributes, the code checks whether `message.destinationName` starts with `APP.HomeAutomation.Manager.ledResultBaseTopic` to make sure that the message's destination topic is the one defined for the results og the commands releated to LEDs.
