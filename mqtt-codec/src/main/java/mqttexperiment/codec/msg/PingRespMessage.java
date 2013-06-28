package mqttexperiment.codec.msg;

import java.nio.ByteBuffer;

public class PingRespMessage extends AbstractMqttMessage {

    public PingRespMessage() {
        super(Type.PINGRESP, false, 0, false);
    }

    @Override
    public ByteBuffer visit(MqttMessageEncodingVisitor visitor) {
        return visitor.visit(this);
    }

}
