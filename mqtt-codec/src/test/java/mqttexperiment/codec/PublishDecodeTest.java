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
    
    //private byte[] data = Utils.hexStringToByteArray("300D0005746164616D617267677321");
    private byte[] data = Utils.hexStringToByteArray("30AC01001D3432353731303735323238393838322F6D657373616765732F6A736F6E5B7B22676174657761792E72737369223A205B7B2274696D657374616D7022203A20313337343637353831333537352C202276616C756522203A2022373839227D5D7D2C7B22656E67696E652E74656D7065726174757265223A205B7B2274696D657374616D7022203A20313337343637353831333537352C202276616C756522203A2022383532227D5D7D5D");

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
        assertEquals("425710752289882/messages/json",msg.getTopic());
        assertEquals("[{\"gateway.rssi\": [{\"timestamp\" : 1374675813575, \"value\" : \"789\"}]},{\"engine.temperature\": [{\"timestamp\" : 1374675813575, \"value\" : \"852\"}]}]",new String(msg.getPayload()));
        assertNotNull(msg);
    }
   
}