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

import mqttexperiment.codec.msg.PublishMessage;

/**
 * Sub state machine of {@link MqttDecodingStep} for decoding {@link PublishMessage}
 */
enum PublishDecodingStep {

    /** read the topic string name */
    TOPIC_NAME {
        @Override
        public PublishMessage decode(ByteBuffer incoming, PublishDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            String topicName = state.subStrCtxt.state.decode(incoming, state.subStrCtxt);
            if (topicName == null) {
                return null;
            }
            // next step
            state.publish.setTopic(topicName);

            if (state.publish.getQos() == 0) {
                state.st = PAYLOAD;
            } else {
                state.st = MSG_ID_MSB;
            }
            state.consumedByte += 2;
            state.consumedByte += state.subStrCtxt.data.length;

            state.st = PAYLOAD;
            // how many bytes in the payload ?
            byte[] payload = new byte[state.remaining - state.consumedByte];
            state.publish.setPayload(payload);
            return state.st.decode(incoming, state);
        }
    },
    /** read the first byte of the message 16bit identifier */
    MSG_ID_MSB {
        @Override
        public PublishMessage decode(ByteBuffer incoming, PublishDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            state.publish.setMessageId((incoming.get() & 0xFF) << 8);
            state.st = MSG_ID_LSB;
            return state.st.decode(incoming, state);
        }
    },
    /** read the second byte of the message 16bit identifier */
    MSG_ID_LSB {
        @Override
        public PublishMessage decode(ByteBuffer incoming, PublishDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            state.publish.setMessageId(state.publish.getMessageId() | (incoming.get() & 0xFF));
            state.consumedByte += 2;
            state.st = PAYLOAD;
            // how many bytes in the payload ?
            byte[] payload = new byte[state.remaining - state.consumedByte];
            state.publish.setPayload(payload);
            return state.st.decode(incoming, state);
        }
    },
    /** decode the payload */
    PAYLOAD {
        @Override
        public PublishMessage decode(ByteBuffer incoming, PublishDecoderContext state) {
            if (state.consumedByte >= state.remaining) {
                return state.publish;
            }
            
            // how many bytes to decode ?
            while(incoming.remaining() > 0) {
               
               int pos = state.consumedByte - (state.remaining - state.publish.getPayload().length);
               state.publish.getPayload()[pos] = incoming.get();
               state.consumedByte++;
               
               if (state.consumedByte >= state.remaining) {
                   return state.publish;
               }
            }

            // need more bytes
            return null;
        }
    };

    public abstract PublishMessage decode(ByteBuffer incoming, PublishDecoderContext state);
}
