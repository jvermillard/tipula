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

import mqttexperiment.codec.msg.AbstractMqttMessage.Type;

/**
 * State of the main MQTT decoding state machine : {@link MqttDecodingStep}
 */
public class MqttDecoderContext {

    /** current state */
    MqttDecodingStep st = MqttDecodingStep.HEADER;

    /** sub state machine for decoding connect message */
    ConnectDecoderContext connectCtx = null;
    
    /** sub state machine for decoding publish message */
    PublishDecoderContext publishCtx = null;
    
    /** sub state machine for decoding subscribe message */
    SubscribeDecoderContext subscribeCtx = null;
    
    Type type;

    boolean dup;

    int qos;

    boolean retain;

    // remaining length value
    int remainingLength = 0;
  
    /**
     * Clean the state after a successful message decoding.
     */
    void restart() {
        st = MqttDecodingStep.HEADER;
    }
}