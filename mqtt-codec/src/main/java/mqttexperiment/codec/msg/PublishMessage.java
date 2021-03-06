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
package mqttexperiment.codec.msg;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * A publish message, used by a client or the server for publishing a message on a given topic.
 */
public class PublishMessage extends AbstractMqttMessage {

    private String topic;

    private int messageId;

    private byte[] payload;

    /**
     * Create a publish message.
     */
    public PublishMessage(boolean dup, int qos, boolean retain) {
        super(Type.PUBLISH, dup, qos, retain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuffer visit(MqttMessageEncodingVisitor visitor) {
        return visitor.visit(this);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "PublishMessage [topic=" + topic + ", messageId=" + messageId + ", payload=" + Arrays.toString(payload)
                + ", getType()=" + getType() + ", isDup()=" + isDup() + ", getQos()=" + getQos() + ", isRetain()="
                + isRetain() + "]";
    }
}