var APP = APP || {};
APP.homeAutomation = APP.homeAutomation || {};
APP.homeAutomation.Manager = {
  ledCommandBaseTopic: "home/leds/", //BaseTopic means "home/leds/..."
  ledResultBaseTopic: "home/results/leds/",

  //MQTT server setting
  host: "127.0.0.1",
  port: 9001, //Websocket
  clientId: "homeWeb-" + clientId: "homeWeb-" + Math.random().toString(16).substr(2, 8),


  mqttConnectOptions: {
    timeout: 3,
    mqttv: 4, //v3.1.1
    mqttvExplicit: true,
    useSSL: false,
    cleanSession: true
  },

  //Update LED
  updateLed: function (ledId, jscolor) {
    console.log('Send ' + jscolor + 'to LED #' + ledId);
    var payload = {
      "Color": jscolor.toString()
    };

    payloadString = JSON.stringify(payload);
    message = new.Paho.MQTT.Message(payloadString);
    message.destinationName = this.ledCommandBaseTopic + ledId;
    message.qos = 0; //The defalut value is 0

    this.client.send(message);

  },

  //When losing connection
  onConnectionLost: function(responseObject) {
    if (responseObject.errorCode !== 0)
      console.log("onConnection is lost: " + responseObject.errorMessage);
  },

  //When message arriving
  onMessageArrived: function(message) {
    console.log("Message arrived for topic: " + message.destinationName + ", with the following payload: " + message.payloadString);
    if(!message.destinationName.startsWith(APP.homeAutomation.Manager.ledResultBaseTopic)){
      return;
    }

    var ledLocation = message.destinationName.replace (APP.homeAutomation.Manager.ledResultBaseTopic, "");
    var payload = JSON.parse(message.payloadString); //payload: re-definition

    if (ledLocation && payload.color) {
      var statusLedDiv = document.getElementById("ledMessage" + ledLocation);
      statusLedDiv.textContent = "The color of LED set to #" + payload.Color;

      var statusLedCircle = document.getElementById("ledStatus" + ledLocation);

      statusLedCircle.style.fill = "#" + payload.Color;
    },

    //When message delivered
    onMessageDelivered: function(message){
    },



  },


}
