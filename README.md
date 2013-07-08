mqtt-experiment
===============

MQTT asynchronous decoding experiment.

It's an asynchronous MQTT protocol decoder for using with scalable NIO based server or client.

Build with maven : "mvn install" in the codec directory.

For using it is quite simple : 

Decoding ByteBuffer of wire protocol to MQTT high level messages :

```java
 
 ByteBuffer buffer = ... // read from the socket, webscoket, or wathever way you want to receive MQTT messages

 AbstractMqttMessage mqttMsg;
 while ((mqttMsg = decoder.decode(((IoBuffer) message).buf(), ctx)) != null) {
    System.out.println("reveiced the MQTT message : "+mqttMsg); 
 }
```


Encoding MQTT protocol message to Bytebuffer :

```java
 // create a MQTT message using your protocol logic
 // all the MQTT protocol messages (connect, subscribe, publish, ping, etc...) are 
 // available in  the package "mqttexperiment.codec.msg"
 AbstractMqttMessage message = ...

 ByteBuffer buff = encoder.encode(message, null);

 // write the byte buffer to the socket, websocket or wathever way you want for sending MQTT messages
```
