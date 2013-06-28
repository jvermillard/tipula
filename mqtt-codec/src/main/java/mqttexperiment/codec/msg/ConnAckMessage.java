package mqttexperiment.codec.msg;

import java.nio.ByteBuffer;

public class ConnAckMessage extends AbstractMqttMessage {

    private final ReturnCode returnCode;

    public ConnAckMessage(ReturnCode returnCode) {
        super(Type.CONNACK, false, 0, false);
        this.returnCode = returnCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuffer visit(MqttMessageEncodingVisitor visitor) {
        return visitor.visit(this);
    }

    public ReturnCode getReturnCode() {
        return returnCode;
    }

    public static enum ReturnCode {
        ACCEPTED(0), REFUSED_PROTOCOL_VERSION(1), REFUSED_IDENTIFIER_REJECTED(2), REFUSED_SERVER_UNAVAILABLE(3), REFUSED_BAD_NAME_OR_PASSWORD(
                4), REFUSED_NOT_AUTHORIZED(5);

        private final int code;

        private ReturnCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}