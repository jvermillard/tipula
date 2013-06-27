package mqttexperiment.codec;

import java.nio.ByteBuffer;

import mqttexperiment.codec.msg.AbstractMqttMessage;
import mqttexperiment.codec.msg.AbstractMqttMessage.Type;
import mqttexperiment.codec.msg.ConnectMessage;

enum MqttDecodingStep {

    HEADER {
        @Override
        public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderState state) {
            if (incoming.remaining() < 1) {
                return null;
            }
            int header = incoming.get() & 0xFF;
            AbstractMqttMessage.Type type = AbstractMqttMessage.Type.fromCode((header & 0b1111_0000) >> 4);
            int qos = (header & 0b0000_0110) >> 1;
            state.type = type;
            state.dup = (header & 0b0000_1000) != 0;
            state.qos = qos;
            state.retain = (header & 0b0000_0001) != 0;
            state.remainingLength = 0;
            state.st = REMAINING_LENGTH;
            return state.st.decode(incoming,state);
        }
    },
    REMAINING_LENGTH {
        @Override
        public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderState state) {
            if (incoming.remaining() < 1)
                return null;
            int b = incoming.get() & 0xFF;
            state.remainingLength = (state.remainingLength << 7) + (b & 0b0111_1111);
            
            // fix me loop ?
            
            if ((b & 0b1000_0000) == 0) {
                // we are done
                //  now we need to pick the next state for the given
                if (state.type == Type.CONNECT) {
                    state.st = PROTOCOL_NAME;
                    state.subStrCtxt = DecodeString.LEN_MSB.createDecoderState();
                    return state.st.decode(incoming,state);
                } else {
                    throw new java.lang.UnsupportedOperationException("not implemented");
                }
            } else {
                return null;
            }
        }
    },
    PROTOCOL_NAME {
        @Override
        public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderState state) {
            if (incoming.remaining() < 1)
                return null;
            state.protocolName = state.subStrCtxt.state.decode(incoming, state.subStrCtxt);
            if (state.protocolName != null) {
                // next step
                state.st = PROTOCOL_VERSION;
                return state.st.decode(incoming,state);
            } else {
                return null;
            }
        }
    },
    PROTOCOL_VERSION {
        @Override
        public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderState state) {
            if (incoming.remaining() < 1)
                return null;
            state.protocolVersion = incoming.get() & 0xFF;
            // next step
            state.st = CONNECT_FLAGS;
            return state.st.decode(incoming,state);
        }
    },
    CONNECT_FLAGS {
        @Override
        public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderState state) {
            if (incoming.remaining() < 1)
                return null;
            state.connectFlags = incoming.get() & 0xFF;
            // next step
            state.st = KEEP_ALIVE_FLAG_MSB;
            return state.st.decode(incoming,state);
        }
    },
    KEEP_ALIVE_FLAG_MSB {
        @Override
        public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderState state) {
            if (incoming.remaining() < 1)
                return null;
            state.keepAliveTimer = (incoming.get() & 0xFF) << 8;
            state.st = KEEP_ALIVE_FLAG_LSB;
            return state.st.decode(incoming,state);
        }
    },
    KEEP_ALIVE_FLAG_LSB {
        @Override
        public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderState state) {
            if (incoming.remaining() < 1)
                return null;
            state.keepAliveTimer |= (incoming.get() & 0xFF);
            state.st = CLIENT_ID;
            state.subStrCtxt = DecodeString.LEN_MSB.createDecoderState();
            return state.st.decode(incoming,state);
        }
    },
    CLIENT_ID {
        @Override
        public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderState state) {
            if (incoming.remaining() < 1)
                return null;
            String clientId = state.subStrCtxt.state.decode(incoming, state.subStrCtxt);
            if (clientId != null) {
                // next step
                // We populate the connect message
                state.cm = new ConnectMessage(Type.CONNECT,state.dup,state.qos,state.retain,state.protocolVersion,state.connectFlags, state.keepAliveTimer);
                state.cm.setClientId(clientId);
                               
                if(state.cm.isFlagWill()) {
                    // decode will
                    state.st = WILL_TOPIC;
                    state.subStrCtxt = DecodeString.LEN_MSB.createDecoderState();
                    return state.st.decode(incoming,state);
                } else if (state.cm.isFlagUsername()){
                    state.st = USERNAME;
                    state.subStrCtxt = DecodeString.LEN_MSB.createDecoderState();
                    return state.st.decode(incoming,state);
                } else if (state.cm.isFlagPassword()){
                    state.st = PASSWORD;
                    state.subStrCtxt = DecodeString.LEN_MSB.createDecoderState();
                    return state.st.decode(incoming,state);
                } else {
                    // done !
                    state.restart();
                    return state.cm;
                }
            } else {
                return null;
            }
        }
    },
    WILL_TOPIC {
        @Override
        public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderState state) {
            if (incoming.remaining() < 1)
                return null;
            String willTopic = state.subStrCtxt.state.decode(incoming, state.subStrCtxt);
            if (willTopic != null) {
                // next step
                state.cm.setWillTopic(willTopic);
                
                // continue
                state.st = WILL_MESSAGE;
                state.subStrCtxt = DecodeString.LEN_MSB.createDecoderState();
                return state.st.decode(incoming,state);
            } else {
                return null;
            }
        }
    },
    WILL_MESSAGE {
        @Override
        public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderState state) {
            if (incoming.remaining() < 1)
                return null;
            String willMessage = state.subStrCtxt.state.decode(incoming, state.subStrCtxt);
            if (willMessage != null) {
                // next step
                
                state.cm.setWillMessage(willMessage);
                
                if (state.cm.isFlagUsername()) {
                    state.st = USERNAME;
                    state.subStrCtxt = DecodeString.LEN_MSB.createDecoderState();
                    return state.st.decode(incoming,state);    
                } else if (state.cm.isFlagPassword()) {
                    state.st = PASSWORD;
                    state.subStrCtxt = DecodeString.LEN_MSB.createDecoderState();
                    return state.st.decode(incoming,state);
                } else {
                    // done !
                    state.restart();
                    return state.cm;
                }
            } else {
                return null;
            }
        }
    },
    USERNAME {
        @Override
        public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderState state) {
            if (incoming.remaining() < 1)
                return null;
            String username = state.subStrCtxt.state.decode(incoming, state.subStrCtxt);
            if (username != null) {
                // next step
                state.cm.setUsername(username);
                
                if (state.cm.isFlagPassword()) {
                    state.st = PASSWORD;
                    state.subStrCtxt = DecodeString.LEN_MSB.createDecoderState();
                    return state.st.decode(incoming,state);
                } else {
                    state.restart();
                    return state.cm;
                }
            } else {
                return null;
            }
        }
    },
    PASSWORD {
        @Override
        public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderState state) {
            if (incoming.remaining() < 1)
                return null;
            String password = state.subStrCtxt.state.decode(incoming, state.subStrCtxt);
            if (password != null) {
                // next step
                state.cm.setPassword(password);

                // done !
                state.restart();
                return state.cm;
            }
            return null;
        }
    };

    public abstract AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderState state);
}