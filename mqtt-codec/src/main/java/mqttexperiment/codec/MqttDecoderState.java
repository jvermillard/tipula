package mqttexperiment.codec;

import mqttexperiment.codec.msg.AbstractMqttMessage.Type;
import mqttexperiment.codec.msg.ConnectMessage;

/**
 * core FSM of the decoder, sanity ends here.
 */
public class MqttDecoderState {

    MqttDecodingStep st = MqttDecodingStep.HEADER;

    DecodeStringContext subStrCtxt = null;

    Type type;

    boolean dup;

    int qos;

    boolean retain;

    // remaining length value
    int remainingLength = 0;

    // only for connect messages
    String protocolName;

    int protocolVersion;

    int connectFlags;

    int keepAliveTimer = 0;

    ConnectMessage cm;
    
    void restart() {
        st = MqttDecodingStep.HEADER;
    }
}