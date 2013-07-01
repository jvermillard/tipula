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

import org.apache.mina.codec.ProtocolDecoder;
import org.apache.mina.codec.ProtocolDecoderException;

/**
 * An asynchronous MQTT decoder. Consume {@link ByteBuffer} and produce {@link AbstractMqttMessage}
 */
public class MqttDecoder implements ProtocolDecoder<ByteBuffer, AbstractMqttMessage, MqttDecoderContext> {

    @Override
    public MqttDecoderContext createDecoderState() {
        return new MqttDecoderContext();
    }

    @Override
    public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderContext state) {
        return state.st.decode(incoming, state);
    }

    @Override
    public void finishDecode(MqttDecoderContext state) {
        if (state.st != MqttDecodingStep.HEADER) {
            throw new ProtocolDecoderException("A partial message is pending");
        }
    }

}
