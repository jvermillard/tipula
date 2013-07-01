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
 * Subscription message, used by a client to subscribe to a list of topic with a given QoS level.
 *
 */
public class SubscribeMessage extends AbstractMqttMessage {

    private String[] topics;

    private int[] topicQos;

    private int messageId;

    public SubscribeMessage() {
        super(Type.SUBSCRIBE, false, 1, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuffer visit(MqttMessageEncodingVisitor visitor) {
        return visitor.visit(this);
    }

    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }

    public int[] getTopicQos() {
        return topicQos;
    }

    public void setTopicQos(int[] topicQos) {
        this.topicQos = topicQos;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "SubscribeMessage [topics=" + Arrays.toString(topics) + ", topicQos=" + Arrays.toString(topicQos)
                + ", getType()=" + getType() + ", isDup()=" + isDup() + ", getQos()=" + getQos() + ", isRetain()="
                + isRetain() + ", messageId=" + messageId + "]";
    }
}
