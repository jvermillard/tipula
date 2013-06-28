package mqttexperiment.codec;

import mqttexperiment.codec.msg.PublishMessage;

class PublishDecoderContext {
    
    PublishDecodingStep st = PublishDecodingStep.TOPIC_NAME;
    
    DecodeStringContext subStrCtxt = new DecodeStringContext();

    // remaining bytes (the decoded value)
    int remaining;
    
    // bytes consumed over the remaining length value, used for decoding payload
    int consumedByte =0;

    PublishMessage publish;
}
