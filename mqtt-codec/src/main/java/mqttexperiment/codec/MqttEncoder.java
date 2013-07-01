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
import java.nio.ByteOrder;

import mqttexperiment.codec.msg.AbstractMqttMessage;
import mqttexperiment.codec.msg.ConnAckMessage;
import mqttexperiment.codec.msg.ConnectMessage;
import mqttexperiment.codec.msg.DisconnectMessage;
import mqttexperiment.codec.msg.MqttMessageEncodingVisitor;
import mqttexperiment.codec.msg.PingReqMessage;
import mqttexperiment.codec.msg.PingRespMessage;
import mqttexperiment.codec.msg.PublishMessage;
import mqttexperiment.codec.msg.SubAckMessage;
import mqttexperiment.codec.msg.SubscribeMessage;

import org.apache.mina.codec.StatelessProtocolEncoder;


/**
 * Encode {@link AbstractMqttMessage} to {@link ByteBuffer}
 */
public class MqttEncoder implements StatelessProtocolEncoder<AbstractMqttMessage, ByteBuffer> {

    /**
     * {@inheritDoc}
     * not needed, we are state-less
     */
    @Override
    public Void createEncoderState() {
        return null;
    }

    @Override
    public ByteBuffer encode(AbstractMqttMessage msg, Void ctx) {
        ByteBuffer encoded = msg.visit(new MqttMessageEncodingVisitor() {

            @Override
            public ByteBuffer visit(ConnAckMessage msg) {
                ByteBuffer buf = ByteBuffer.allocate(4);
                buf.put(computeHeader(msg));
                buf.put((byte) 0x2);
                buf.put((byte) 0);
                buf.put((byte) msg.getReturnCode().getCode());
                buf.flip();
                return buf;
            }

            @Override
            public ByteBuffer visit(ConnectMessage msg) {
                throw new java.lang.UnsupportedOperationException("not implemented");
            }

            @Override
            public ByteBuffer visit(PingReqMessage msg) {
                throw new java.lang.UnsupportedOperationException("not implemented");
            }

            @Override
            public ByteBuffer visit(PingRespMessage msg) {
                ByteBuffer buf = ByteBuffer.allocate(2);
                buf.put(computeHeader(msg));
                buf.put((byte) 0);
                buf.flip();
                return buf;
            }

            @Override
            public ByteBuffer visit(PublishMessage msg) {
                throw new java.lang.UnsupportedOperationException("not implemented");
            }

            @Override
            public ByteBuffer visit(DisconnectMessage msg) {
                throw new java.lang.UnsupportedOperationException("not implemented");
            }

            @Override
            public ByteBuffer visit(SubscribeMessage msg) {
                throw new java.lang.UnsupportedOperationException("not implemented");
            }

            @Override
            public ByteBuffer visit(SubAckMessage msg) {
                ByteBuffer buf = ByteBuffer.allocate(1 + 2 + 1 + msg.getGrantedQos().length);
                buf.order(ByteOrder.BIG_ENDIAN);
                buf.put(computeHeader(msg));
                buf.put((byte) (msg.getGrantedQos().length + 2));
                // remaining 
                buf.putShort((short) msg.getMessageId());
                for (int qos : msg.getGrantedQos()) {
                    buf.put((byte) qos);
                }
                buf.flip();
                return buf;
            }
        });
        return encoded;
    }

    private byte computeHeader(AbstractMqttMessage msg) {
        return (byte) ((msg.getType().getCode() << 4) | (msg.isDup() ? 0x8 : 0) | (msg.getQos() << 1) | (msg.isRetain() ? 0x1
                : 0));
    }
}