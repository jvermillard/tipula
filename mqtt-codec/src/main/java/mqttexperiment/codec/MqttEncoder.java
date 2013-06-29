package mqttexperiment.codec;

import java.nio.ByteBuffer;

import mqttexperiment.codec.msg.AbstractMqttMessage;
import mqttexperiment.codec.msg.ConnAckMessage;
import mqttexperiment.codec.msg.ConnectMessage;
import mqttexperiment.codec.msg.DisconnectMessage;
import mqttexperiment.codec.msg.MqttMessageEncodingVisitor;
import mqttexperiment.codec.msg.PingReqMessage;
import mqttexperiment.codec.msg.PingRespMessage;
import mqttexperiment.codec.msg.PublishMessage;

import org.apache.mina.codec.StatelessProtocolEncoder;

public class MqttEncoder implements StatelessProtocolEncoder<AbstractMqttMessage, ByteBuffer> {

    /**
     * {@inheritDoc}
     * not needed, we are state-less
     */
    @Override
    public Void createEncoderState() {
        return null;
    }

    @Override
    public ByteBuffer encode(AbstractMqttMessage msg, Void ctx) {
        ByteBuffer encoded = msg.visit(new MqttMessageEncodingVisitor() {

            @Override
            public ByteBuffer visit(ConnAckMessage msg) {
                ByteBuffer buf = ByteBuffer.allocate(4);
                buf.put(computeHeader(msg));
                buf.put((byte) 0x2);
                buf.put((byte) 0);
                buf.put((byte) msg.getReturnCode().getCode());
                buf.flip();
                return buf;
            }

            @Override
            public ByteBuffer visit(ConnectMessage msg) {
                throw new java.lang.UnsupportedOperationException("not implemented");
            }

            @Override
            public ByteBuffer visit(PingReqMessage msg) {
                throw new java.lang.UnsupportedOperationException("not implemented");
            }

            @Override
            public ByteBuffer visit(PingRespMessage msg) {
                ByteBuffer buf = ByteBuffer.allocate(2);
                buf.put(computeHeader(msg));
                buf.put((byte) 0);
                buf.flip();
                return buf;
            }
            
            @Override
            public ByteBuffer visit(PublishMessage msg) {
                throw new java.lang.UnsupportedOperationException("not implemented");
            }
            
            @Override
            public ByteBuffer visit(DisconnectMessage msg) {
                throw new java.lang.UnsupportedOperationException("not implemented");
            }
        });
        return encoded;
    }

    private byte computeHeader(AbstractMqttMessage msg) {
        return (byte) ((msg.getType().getCode() << 4) | (msg.isDup() ? 0x8 : 0) | (msg.getQos() << 1) | (msg.isRetain() ? 0x1
                : 0));
    }
}