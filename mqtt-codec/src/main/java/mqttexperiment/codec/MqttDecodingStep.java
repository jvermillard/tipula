package mqttexperiment.codec;

import java.nio.ByteBuffer;

import mqttexperiment.codec.msg.AbstractMqttMessage;
import mqttexperiment.codec.msg.ConnectMessage;
import mqttexperiment.codec.msg.DisconnectMessage;
import mqttexperiment.codec.msg.PingReqMessage;
import mqttexperiment.codec.msg.PublishMessage;

enum MqttDecodingStep {

    HEADER {
        @Override
        public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderContext state) {
            if (incoming.remaining() < 1) {
                return null;
            }
            int header = incoming.get() & 0xFF;
            AbstractMqttMessage.Type type = AbstractMqttMessage.Type.fromCode((header & 0xF0) >> 4);
            int qos = (header & 0x6) >> 1;
            state.type = type;
            state.dup = (header & 0x8) != 0;
            state.qos = qos;
            state.retain = (header & 0x1) != 0;
            state.remainingLength = 0;
            state.st = REMAINING_LENGTH;
            return state.st.decode(incoming, state);
        }
    },
    REMAINING_LENGTH {
        @Override
        public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderContext state) {
            
            while (incoming.remaining() > 0) {
                int b = incoming.get() & 0xFF;
                state.remainingLength = (state.remainingLength << 7) + (b & 0x7F);
    
                if ((b & 0x80) == 0) {
                    // we are done decoding the remaining length, compute next step
                    switch (state.type) {
                    case CONNECT:
                        // spawn a sub state machine for connect message decoding
                        state.st = CONNECT_MSG;
                        state.connectCtx = new ConnectDecoderContext();
                        state.connectCtx.cm = new ConnectMessage(state.dup, state.qos, state.retain);
                        return state.st.decode(incoming, state);
                    case PINGREQ:
                        state.restart();
                        return PingReqMessage.PING_INSTANCE;
                    case DISCONNECT:
                        state.restart();
                        return DisconnectMessage.DISCONNECT_INSTANCE;
                    case PUBLISH:
                        state.st = PUBLISH;
                        state.publishCtx = new PublishDecoderContext();
                        state.publishCtx.remaining = state.remainingLength;
                        state.publishCtx.publish = new PublishMessage(state.dup, state.qos, state.retain);
                        return state.st.decode(incoming, state);
                    default:
                        throw new java.lang.UnsupportedOperationException("not implemented : "+state.type.name());
                    }
                }
            }
            // need more bytes
            return null;
        }
    },
    CONNECT_MSG {
        @Override
        public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            ConnectMessage msg = state.connectCtx.st.decode(incoming, state.connectCtx);
            if (msg !=null) {
                // reset
                state.restart();
                return msg;
            } else {
                return null;
            }
        }
    },
    PUBLISH {
        @Override
        public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            PublishMessage msg = state.publishCtx.st.decode(incoming, state.publishCtx);
            if (msg !=null) {
                // reset
                state.restart();
                return msg;
            } else {
                return null;
            }
        }
    };

    public abstract AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderContext state);
}