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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.ByteBuffer;

import mqttexperiment.codec.msg.PublishMessage;

import org.junit.Test;

public class PublishDecodeTest {
    
    private byte[] data = Utils.hexStringToByteArray("300D0005746164616D617267677321");

    @Test
    public void decode_publish_message() {

        MqttDecoder dec = new MqttDecoder();
        MqttDecoderContext state = dec.createDecoderState();

        System.err.println(dec.decode(ByteBuffer.wrap(data), state));
    }

    @Test
    public void decode_byte_by_byte_publish_message() {
        MqttDecoder dec = new MqttDecoder();
        MqttDecoderContext state = dec.createDecoderState();
        
        PublishMessage msg;
        int index =0;
        do {
            byte[] chunk = new byte[1];
            chunk[0] = data[index++];
            msg = (PublishMessage) dec.decode(ByteBuffer.wrap(chunk), state);
            if(msg != null) {
                System.err.println(msg);
                break;
            }
        } while(index<data.length);
        
        assertEquals(0,msg.getMessageId());
        System.err.println(new String(msg.getPayload()));
        assertEquals("tadam",msg.getTopic());
        assertEquals("arggs!",new String(msg.getPayload()));
        assertNotNull(msg);
    }
   
}