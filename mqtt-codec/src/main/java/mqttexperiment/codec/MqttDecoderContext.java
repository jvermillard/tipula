package mqttexperiment.codec;

import mqttexperiment.codec.msg.AbstractMqttMessage.Type;

/**
 * core FSM of the decoder, sanity ends here.
 */
public class MqttDecoderContext {

    MqttDecodingStep st = MqttDecodingStep.HEADER;

    DecodeStringContext subStrCtxt = null;

    ConnectDecoderContext connectCtx = null;
    
    PublishDecoderContext publishCtx = null;
    
    Type type;

    boolean dup;

    int qos;

    boolean retain;

    // remaining length value
    int remainingLength = 0;
        
    void restart() {
        st = MqttDecodingStep.HEADER;
    }
}