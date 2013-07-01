/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package mqttexperiment.codec;

import java.nio.ByteBuffer;

import mqttexperiment.codec.msg.ConnectMessage;

/**
 * Sub state machine of {@link MqttDecodingStep} for decoding {@link ConnectMessage}
 */
enum ConnectDecodingStep {
    /** read string for protocol name */
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
            return state.st.decode(incoming, state);
        }
    },
    /** read string for protocol version */
    PROTOCOL_VERSION {
        @Override
        public ConnectMessage decode(ByteBuffer incoming, ConnectDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            state.cm.setVersion(incoming.get() & 0xFF);
            // next step
            state.st = CONNECT_FLAGS;
            return state.st.decode(incoming, state);
        }
    },
    /** connect flags for knowing which field are present */
    CONNECT_FLAGS {
        @Override
        public ConnectMessage decode(ByteBuffer incoming, ConnectDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            state.cm.setConnectFlags(incoming.get() & 0xFF);

            // next step
            state.st = KEEP_ALIVE_FLAG_MSB;
            return state.st.decode(incoming, state);
        }
    },
    /** first part of the keep-alive 16bit value */
    KEEP_ALIVE_FLAG_MSB {
        @Override
        public ConnectMessage decode(ByteBuffer incoming, ConnectDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            state.cm.setKeepAlive((incoming.get() & 0xFF) << 8);
            state.st = KEEP_ALIVE_FLAG_LSB;
            return state.st.decode(incoming, state);
        }
    },
    /** second part of the keep-alive 16bit value */
    KEEP_ALIVE_FLAG_LSB {
        @Override
        public ConnectMessage decode(ByteBuffer incoming, ConnectDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            state.cm.setKeepAlive(state.cm.getKeepAlive() | (incoming.get() & 0xFF));
            state.st = CLIENT_ID;
            state.subStrCtxt = DecodeString.LEN_MSB.createDecoderState();
            return state.st.decode(incoming, state);
        }
    },
    /** read string for client identifier */
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

            if (state.cm.isFlagWill()) {
                // decode will
                state.st = WILL_TOPIC;
                state.subStrCtxt = DecodeString.LEN_MSB.createDecoderState();
                return state.st.decode(incoming, state);
            } else if (state.cm.isFlagUsername()) {
                state.st = USERNAME;
                state.subStrCtxt = DecodeString.LEN_MSB.createDecoderState();
                return state.st.decode(incoming, state);
            } else if (state.cm.isFlagPassword()) {
                state.st = PASSWORD;
                state.subStrCtxt = DecodeString.LEN_MSB.createDecoderState();
                return state.st.decode(incoming, state);
            } else {
                // done !
                return state.cm;
            }
        }
    },
    /** read string for the will topic */
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
            return state.st.decode(incoming, state);
        }
    },
    /** read string for the will message to post when the client disconnect */
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
                return state.st.decode(incoming, state);
            } else if (state.cm.isFlagPassword()) {
                state.st = PASSWORD;
                state.subStrCtxt = DecodeString.LEN_MSB.createDecoderState();
                return state.st.decode(incoming, state);
            } else {
                // done !
                return state.cm;
            }
        }
    },
    /** read string for the username */
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
                return state.st.decode(incoming, state);
            } else {
                return state.cm;
            }
        }
    },
    /** read string for the password */
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
