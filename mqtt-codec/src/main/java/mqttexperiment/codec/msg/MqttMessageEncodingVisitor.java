package mqttexperiment.codec.msg;

import java.nio.ByteBuffer;

public interface MqttMessageEncodingVisitor {

    ByteBuffer visit(ConnectMessage msg);
    
    ByteBuffer visit(ConnAckMessage msg);
    
    ByteBuffer visit(PingReqMessage msg);
}
