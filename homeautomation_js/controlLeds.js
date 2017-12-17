var mqtt = require("mqtt")

var host = "127.0.0.1"
var port = 9001 //ws
var client = mqtt.connect("ws://" + host + ":" + port)
//var client = mqtt.connect("wss//" + host + ":" + port) //Working with TLS

var ledCommandBaseTopic = "home/leds/"
var ledResultBaseTopic = "home/results/leds/"

client.on("connect", function() {
  console.log("Connected to the MQTT-server")
  var topicFilters = Array()
  for (i = 1; i < 4; i++){
    topicFilters.push(ledCommandBaseTopic + i)
  }
  client.subscribe(topicFilters) //Default QoS = 0
})

client.on('message', function (topic, message) {
  var payloadString = message.toString()
  console.log("Message arrived for topic: " + topic + ", Payload: " + payloadString)
  if (!topic.startsWith(ledCommandBaseTopic)) {
    return;
  }

  var ledLocation = topic.replace(ledCommandBaseTopic, "")
  var payload = JSON.parse(payloadString)
  if (ledLocation && payload.Color ) {
    console.log("LED # " + ledLocation + " to " + payload.Color)
  }

  var resultMessagePayload = {
    "Color" : payload.Color
  }
  resultMessagePayloadString = JSON.stringify(resultMessagePayload)
  client.publish(ledResultBaseTopic + ledLocation, resultMessagePayloadString)

})
