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

import mqttexperiment.codec.msg.ConnectMessage;

import org.junit.Test;

public class DecodeConnectTest {

    private byte[] data = Utils.hexStringToByteArray("103B00064D514973647003CE000C000C6D794964656E746966696572000B646561642F636C69656E74000661726767732100036A6F6500057469676572");

    @Test
    public void decode_connect_message() {


        MqttDecoder dec = new MqttDecoder();
        MqttDecoderContext state = dec.createDecoderState();

        System.err.println(dec.decode(ByteBuffer.wrap(data), state));
    }

    @Test
    public void decode_byte_by_byte_connect_message() {
        MqttDecoder dec = new MqttDecoder();
        MqttDecoderContext state = dec.createDecoderState();
        
        ConnectMessage msg;
        int index =0;
        do {
            byte[] chunk = new byte[1];
            chunk[0] = data[index++];
            msg = (ConnectMessage)dec.decode(ByteBuffer.wrap(chunk), state);
            if(msg != null) {
                System.err.println(msg);
                break;
            }
        } while(index<data.length);
        
        assertNotNull(msg);
        assertEquals("myIdentifier", msg.getClientId());
        assertEquals("joe", msg.getUsername());
        assertEquals("tiger", msg.getPassword());
        assertEquals(12,msg.getKeepAlive());
        assertEquals("MQIsdp",msg.getProtocolName());
        assertEquals(3,msg.getVersion());
        assertEquals("arggs!",msg.getWillMessage());
        assertEquals("dead/client",msg.getWillTopic());
    }

}