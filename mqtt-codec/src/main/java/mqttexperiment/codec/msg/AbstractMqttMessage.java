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

/**
 * Base MQTT message. Ancestor of all the MQTT protocol messages.
 * 
 */
public abstract class AbstractMqttMessage {

    private Type type;

    private boolean dup;

    private int qos;

    private boolean retain;

    /**
     * Build a MQTT message.
     * 
     * @param type the type of the message, from the list of know MQTT messages
     * @param dup is a duplicate message
     * @param qos the QOS level
     * @param retain retain flag
     */
    public AbstractMqttMessage(Type type, boolean dup, int qos, boolean retain) {
        super();
        this.type = type;
        this.dup = dup;
        this.qos = qos;
        this.retain = retain;
    }

    /**
     * Visit the message for encoding using polymorphism. (follow the GoF Visitor pattern)
     * @param visitor the visitor to call for the correct message type.
     * @return the result of the visiting
     */
    public abstract ByteBuffer visit(MqttMessageEncodingVisitor visitor);

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isDup() {
        return dup;
    }

    public void setDup(boolean dup) {
        this.dup = dup;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean isRetain() {
        return retain;
    }

    public void setRetain(boolean retain) {
        this.retain = retain;
    }

    /**
     * The list of known MQTT message types
     */
    public enum Type {
        CONNECT(1), CONNACK(2), PUBLISH(3), PUBACK(4), PUBREC(5), PUBREL(6), PUBCOMP(7), SUBSCRIBE(8), SUBACK(9), UNSUBSCRIBE(
                10), UNSUBACK(11), PINGREQ(12), PINGRESP(13), DISCONNECT(14);

        private final int code;

        private Type(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static Type fromCode(int code) {
            for (Type t : Type.values()) {
                if (t.getCode() == code) {
                    return t;
                }
            }
            return null;
        }
    }
}
