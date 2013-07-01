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

import java.util.ArrayList;
import java.util.List;

import mqttexperiment.codec.msg.SubscribeMessage;

/**
 * State of the {@link SubscribeDecodingStep} state machine.
 */
public class SubscribeDecoderContext {
    
    SubscribeDecodingStep st = SubscribeDecodingStep.MSG_ID_MSB;
    
    SubscribeMessage subscribe;
    
    // remaining bytes (the decoded value)
    int remaining;
    
    // the byte consumed from the variable header and the payload
    int consumedByte;
    
    DecodeStringContext subStrCtxt = new DecodeStringContext();
    
    List<String> topics = new ArrayList<String>();
    List<Integer> topicsQos = new ArrayList<Integer>();
    
}
