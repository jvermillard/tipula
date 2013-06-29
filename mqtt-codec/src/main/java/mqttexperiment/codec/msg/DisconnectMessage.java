package mqttexperiment.codec.msg;

import java.nio.ByteBuffer;

public class DisconnectMessage extends AbstractMqttMessage {

    // used to reduce the GC pressure, since this message is immutable
    public static final DisconnectMessage DISCONNECT_INSTANCE = new DisconnectMessage();

    public DisconnectMessage() {
        super(Type.DISCONNECT, false, 0, false);
    }

    @Override
    public ByteBuffer visit(MqttMessageEncodingVisitor visitor) {
        return visitor.visit(this);
    }
}
