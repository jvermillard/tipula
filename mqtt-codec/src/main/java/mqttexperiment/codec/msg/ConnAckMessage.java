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
 * A connect acknowledge message, send by the server for acknowledging (with or without error) a {@link ConnectMessage}.
 * 
 */
public class ConnAckMessage extends AbstractMqttMessage {

    private final ReturnCode returnCode;

    /**
     * Create a connection acknowledge message.
     * @param returnCode the acknowledge return code (success, or error).
     */
    public ConnAckMessage(ReturnCode returnCode) {
        super(Type.CONNACK, false, 0, false);
        this.returnCode = returnCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuffer visit(MqttMessageEncodingVisitor visitor) {
        return visitor.visit(this);
    }

    public ReturnCode getReturnCode() {
        return returnCode;
    }

    /**
     * List of known MQTT message code.
     */
    public static enum ReturnCode {
        ACCEPTED(0), REFUSED_PROTOCOL_VERSION(1), REFUSED_IDENTIFIER_REJECTED(2), REFUSED_SERVER_UNAVAILABLE(3), REFUSED_BAD_NAME_OR_PASSWORD(
                4), REFUSED_NOT_AUTHORIZED(5);

        private final int code;

        private ReturnCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((returnCode == null) ? 0 : returnCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConnAckMessage other = (ConnAckMessage) obj;
        if (returnCode != other.returnCode)
            return false;
        return true;
    }
}