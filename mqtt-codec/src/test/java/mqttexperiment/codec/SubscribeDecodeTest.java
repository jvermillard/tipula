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

import org.junit.Assert;
import org.junit.Test;

public class SubscribeDecodeTest {

    private byte[] data = Utils.hexStringToByteArray("820900010004626c656800");

    //private byte[] data = hexStringToByteArray("820E0000000341424300000344454601");
    @Test
    public void decode_subscribe_message() {

        MqttDecoder dec = new MqttDecoder();
        MqttDecoderContext state = dec.createDecoderState();

        System.err.println(dec.decode(ByteBuffer.wrap(data), state));
    }

    @Test
    public void decode_byte_by_byte_subscribe_message() {
        MqttDecoder dec = new MqttDecoder();
        MqttDecoderContext state = dec.createDecoderState();

        AbstractMqttMessage msg;
        int index = 0;
        do {
            byte[] chunk = new byte[1];
            chunk[0] = data[index++];
            msg = dec.decode(ByteBuffer.wrap(chunk), state);
            if (msg != null) {
                System.err.println(msg);
                break;
            }
        } while (index < data.length);

        System.err.println(state.st.name());
        Assert.assertNotNull(msg);
    }
}
