package mqttexperiment.codec;

import java.nio.ByteBuffer;

import mqttexperiment.codec.msg.ConnectMessage;

enum ConnectDecodingStep {
    PROTOCOL_NAME {
        @Override
        public ConnectMessage decode(ByteBuffer incoming, ConnectDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            String protocolName = state.subStrCtxt.state.decode(incoming, state.subStrCtxt);
            if (protocolName == null) {
                return null;
            }
            // next step
            state.cm.setProtocolName(protocolName);
            state.st = PROTOCOL_VERSION;
            return state.st.decode(incoming,state);
        }
    },
    PROTOCOL_VERSION {
        @Override
        public ConnectMessage decode(ByteBuffer incoming, ConnectDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            state.cm.setVersion(incoming.get() & 0xFF);
            // next step
            state.st = CONNECT_FLAGS;
            return state.st.decode(incoming,state);
        }
    },
    CONNECT_FLAGS {
        @Override
        public ConnectMessage decode(ByteBuffer incoming, ConnectDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            state.cm.setConnectFlags(incoming.get() & 0xFF);
            
            // next step
            state.st = KEEP_ALIVE_FLAG_MSB;
            return state.st.decode(incoming,state);
        }
    },
    KEEP_ALIVE_FLAG_MSB {
        @Override
        public ConnectMessage decode(ByteBuffer incoming, ConnectDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            state.cm.setKeepAlive( (incoming.get() & 0xFF) << 8);
            state.st = KEEP_ALIVE_FLAG_LSB;
            return state.st.decode(incoming,state);
        }
    },
    KEEP_ALIVE_FLAG_LSB {
        @Override
        public ConnectMessage decode(ByteBuffer incoming, ConnectDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            state.cm.setKeepAlive( state.cm.getKeepAlive() | (incoming.get() & 0xFF) );
            state.st = CLIENT_ID;
            state.subStrCtxt = DecodeString.LEN_MSB.createDecoderState();
            return state.st.decode(incoming,state);
        }
    },
    CLIENT_ID {
        @Override
        public ConnectMessage decode(ByteBuffer incoming, ConnectDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            String clientId = state.subStrCtxt.state.decode(incoming, state.subStrCtxt);
            if (clientId == null) {
                return null;
            }
            
            // next step
            // We populate the connect message
            
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
                return state.cm;
            }
        }
    },
    WILL_TOPIC {
        @Override
        public ConnectMessage decode(ByteBuffer incoming, ConnectDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            String willTopic = state.subStrCtxt.state.decode(incoming, state.subStrCtxt);
            if (willTopic == null) {
                return null;
            }
            // next step
            state.cm.setWillTopic(willTopic);
            
            // continue
            state.st = WILL_MESSAGE;
            state.subStrCtxt = DecodeString.LEN_MSB.createDecoderState();
            return state.st.decode(incoming,state);
        }
    },
    WILL_MESSAGE {
        @Override
        public ConnectMessage decode(ByteBuffer incoming, ConnectDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            String willMessage = state.subStrCtxt.state.decode(incoming, state.subStrCtxt);
            if (willMessage == null) {
                return null;
            }
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
                return state.cm;
            }
        }
    },
    USERNAME {
        @Override
        public ConnectMessage decode(ByteBuffer incoming, ConnectDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            String username = state.subStrCtxt.state.decode(incoming, state.subStrCtxt);
            if (username == null) {
                return null;
            }
            // next step
            state.cm.setUsername(username);
            
            if (state.cm.isFlagPassword()) {
                state.st = PASSWORD;
                state.subStrCtxt = DecodeString.LEN_MSB.createDecoderState();
                return state.st.decode(incoming,state);
            } else {
                return state.cm;
            }
        }
    },
    PASSWORD {
        @Override
        public ConnectMessage decode(ByteBuffer incoming, ConnectDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            String password = state.subStrCtxt.state.decode(incoming, state.subStrCtxt);
            if (password == null) {
                return null;
            }
            // next step
            state.cm.setPassword(password);

            // done !
            return state.cm;
        }
    };
    
    public abstract ConnectMessage decode(ByteBuffer incoming, ConnectDecoderContext state);
}
