package mqttexperiment.codec.msg;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class PublishMessage extends AbstractMqttMessage {

    private String topic;
    
    private int messageId;
    
    private byte[] payload;
    
    public PublishMessage(boolean dup, int qos, boolean retain) {
        super(Type.PUBLISH, dup, qos, retain);
    }

    @Override
    public ByteBuffer visit(MqttMessageEncodingVisitor visitor) {
        return visitor.visit(this);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "PublishMessage [topic=" + topic + ", messageId=" + messageId + ", payload=" + Arrays.toString(payload)
                + ", getType()=" + getType() + ", isDup()=" + isDup() + ", getQos()=" + getQos() + ", isRetain()="
                + isRetain() + "]";
    }
}