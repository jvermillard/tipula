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
 * Subscription acknowledge message. Used by the server to acknowledge a {@link SubscribeMessage} request from the client. 
 */
public class SubAckMessage extends AbstractMqttMessage {

    private int messageId;

    private int[] grantedQos;

    /**
     * Create a subscription acknowledge message.
     */
    public SubAckMessage() {
        super(Type.SUBACK, false, 0, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuffer visit(MqttMessageEncodingVisitor visitor) {
        return visitor.visit(this);
    }

    public int[] getGrantedQos() {
        return grantedQos;
    }

    public void setGrantedQos(int[] grantedQos) {
        this.grantedQos = grantedQos;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "SubAckMessage [messageId=" + messageId + ", grantedQos=" + Arrays.toString(grantedQos) + ", getType()="
                + getType() + ", isDup()=" + isDup() + ", getQos()=" + getQos() + ", isRetain()=" + isRetain() + "]";
    }

}
