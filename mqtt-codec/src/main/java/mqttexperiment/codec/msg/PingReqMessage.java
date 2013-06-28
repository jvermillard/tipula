package mqttexperiment.codec.msg;

import java.nio.ByteBuffer;

public class PingReqMessage extends AbstractMqttMessage {

    // used to reduce the GC pressure, since this message is immutable
    public static final PingReqMessage PING_INSTANCE = new PingReqMessage();
    
    public PingReqMessage() {
        super(Type.PINGREQ, false, 0, false);
    }

    @Override
    public ByteBuffer visit(MqttMessageEncodingVisitor visitor) {
        return visitor.visit(this);
    }

    
}
