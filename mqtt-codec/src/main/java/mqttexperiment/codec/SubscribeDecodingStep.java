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
import java.util.ArrayList;

import mqttexperiment.codec.msg.SubscribeMessage;

/**
 * Sub state machine of {@link MqttDecodingStep} for decoding {@link SubscribeMessage}
 */
enum SubscribeDecodingStep {
    /** read the first byte of the 16bit message identifier */
    MSG_ID_MSB {
        @Override
        public SubscribeMessage decode(ByteBuffer incoming, SubscribeDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            state.subscribe.setMessageId((incoming.get() & 0xFF) << 8);
            state.st = MSG_ID_LSB;
            return state.st.decode(incoming, state);
        }
    },
    /** read the second byte of the 16bit message identifier */
    MSG_ID_LSB {
        @Override
        public SubscribeMessage decode(ByteBuffer incoming, SubscribeDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            state.subscribe.setMessageId(state.subscribe.getMessageId() | (incoming.get() & 0xFF));
            state.consumedByte += 2;
            state.st = TOPIC;
            state.subStrCtxt.state = DecodeString.LEN_MSB;
            state.topics = new ArrayList<String>();

            // how many bytes in the payload ?
            return state.st.decode(incoming, state);
        }
    },
    /** read a topic string (until end of payload) */
    TOPIC {
        @Override
        public SubscribeMessage decode(ByteBuffer incoming, SubscribeDecoderContext state) {
            if (state.consumedByte >= state.remaining) {
                // done !
                state.subscribe.setTopics(state.topics.toArray(STR_ARRAY));
                int[] qos = new int[state.topicsQos.size()];
                for (int i = 0; i < qos.length; i++) {
                    qos[i] = state.topicsQos.get(i).intValue();
                }
                state.subscribe.setTopicQos(qos);

                return state.subscribe;
            }

            if (incoming.remaining() < 1)
                return null;

            String topic = state.subStrCtxt.state.decode(incoming, state.subStrCtxt);
            if (topic == null) {
                return null;
            }

            state.topics.add(topic);
            state.consumedByte += 2 + state.subStrCtxt.data.length;
            // decode the QoS
            state.st = TOPIC_QOS;
            return state.st.decode(incoming, state);
        }
    },
    /** read the topic requested QoS */
    TOPIC_QOS {
        @Override
        public SubscribeMessage decode(ByteBuffer incoming, SubscribeDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            int qos = incoming.get() & 0xFF;
            state.topicsQos.add(qos);
            state.consumedByte++;
            state.st = TOPIC;
            return state.st.decode(incoming, state);
        }
    };

    public abstract SubscribeMessage decode(ByteBuffer incoming, SubscribeDecoderContext state);

    private static final String[] STR_ARRAY = new String[] {};
}
