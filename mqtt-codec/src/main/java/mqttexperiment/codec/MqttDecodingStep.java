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

import mqttexperiment.codec.msg.AbstractMqttMessage;
import mqttexperiment.codec.msg.ConnectMessage;
import mqttexperiment.codec.msg.DisconnectMessage;
import mqttexperiment.codec.msg.PingReqMessage;
import mqttexperiment.codec.msg.PublishMessage;
import mqttexperiment.codec.msg.SubscribeMessage;

/**
 * Main state machine for decoding MQTT messages.
 * For the most complex message a dedicated sub machine will handle it.
 */
enum MqttDecodingStep {

    /** read the header byte */
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
    /** read the remaining length, loop until the  */
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
                    case SUBSCRIBE:
                        state.st = SUBSCRIBE;
                        state.subscribeCtx = new SubscribeDecoderContext();
                        state.subscribeCtx.subscribe = new SubscribeMessage();
                        state.subscribeCtx.remaining = state.remainingLength;
                        return state.st.decode(incoming, state);
                    default:
                        throw new java.lang.UnsupportedOperationException("not implemented : " + state.type.name());
                    }
                }
            }
            // need more bytes
            return null;
        }
    },
    /** decode a {@link ConnectMessage} with a {@link ConnectDecodingStep} sub state machine */
    CONNECT_MSG {
        @Override
        public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            ConnectMessage msg = state.connectCtx.st.decode(incoming, state.connectCtx);
            if (msg != null) {
                // reset
                state.restart();
                return msg;
            } else {
                return null;
            }
        }
    },
    /** decode a {@link PublishMessage} with a {@link PublishDecodingStep} sub state machine */
    PUBLISH {
        @Override
        public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            PublishMessage msg = state.publishCtx.st.decode(incoming, state.publishCtx);
            if (msg != null) {
                // reset
                state.restart();
                return msg;
            } else {
                return null;
            }
        }
    },
    /** decode a {@link SubscribeMessage} with a {@link SubscribeDecodingStep} sub state machine */
    SUBSCRIBE {
        @Override
        public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            SubscribeMessage msg = state.subscribeCtx.st.decode(incoming, state.subscribeCtx);
            if (msg != null) {
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