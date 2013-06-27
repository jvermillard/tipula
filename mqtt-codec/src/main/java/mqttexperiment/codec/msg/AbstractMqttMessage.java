package mqttexperiment.codec.msg;

import java.nio.ByteBuffer;

public abstract class AbstractMqttMessage {

    private Type type;

    private boolean dup;

    private int qos;

    private boolean retain;

    public AbstractMqttMessage(Type type, boolean dup, int qos, boolean retain) {
        super();
        this.type = type;
        this.dup = dup;
        this.qos = qos;
        this.retain = retain;
    }
    
    public abstract ByteBuffer visit(MqttMessageEncodingVisitor visitor);

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isDup() {
        return dup;
    }

    public void setDup(boolean dup) {
        this.dup = dup;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean isRetain() {
        return retain;
    }

    public void setRetain(boolean retain) {
        this.retain = retain;
    }

    public enum Type {
        CONNECT(1), CONNACK(2), PUBLISH(3), PUBACK(4), PUBREC(5), PUBREL(6), PUBCOMP(7), SUBSCRIBE(8), SUBACK(9), UNSUBSCRIBE(
                10), UNSUBACK(11), PINGREQ(12), PINGRESP(13), DISCONNECT(14);

        private final int code;

        private Type(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static Type fromCode(int code) {
            for (Type t : Type.values()) {
                if (t.getCode() == code) {
                    return t;
                }
            }
            return null;
        }
    }
}
