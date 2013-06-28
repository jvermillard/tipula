package mqttexperiment.codec;

import mqttexperiment.codec.msg.ConnectMessage;

class ConnectDecoderContext {

    ConnectDecodingStep st = ConnectDecodingStep.PROTOCOL_NAME;
    
    DecodeStringContext subStrCtxt = new DecodeStringContext();

    String protocolName;

    int connectFlags;

    int keepAliveTimer = 0;

    ConnectMessage cm;

}
