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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.apache.mina.codec.ProtocolDecoder;
import org.apache.mina.codec.ProtocolDecoderException;

/**
 * Sub state machine used for decoding a UTF8 encoded string.
 * The UTF8 string is prefixed by the length (in bytes) in the big endian order.
 */
enum DecodeString implements ProtocolDecoder<ByteBuffer, String, DecodeStringContext> {

    /** read the first byte of the string byte length */
    LEN_MSB {
        public String decode(ByteBuffer b, DecodeStringContext ctx) {
            if (b.remaining() == 0) {
                return null;
            }
            ctx.length = (b.get() & 0xFF) << 8;
            ctx.state = LEN_LSB;
            return ctx.state.decode(b, ctx);
        }
    },
    /** read the second byte of the string byte length */
    LEN_LSB {
        public String decode(ByteBuffer b, DecodeStringContext ctx) {
            if (b.remaining() == 0) {
                return null;
            }
            ctx.length |= (b.get() & 0xFF);
            if (ctx.length == 0) {
                ctx.state = DONE;
                return "";
            }
            ctx.data = new byte[ctx.length];
            ctx.state = CONTENT;
            return ctx.state.decode(b, ctx);
        }
    },
    CONTENT {
        public String decode(ByteBuffer b, DecodeStringContext ctx) {
            if (b.remaining() == 0) {
                return null;
            }
            // read while no more or filled
            while (b.remaining() > 0 && ctx.filled < ctx.length) {
                ctx.data[ctx.filled++] = b.get();
            }
            if (ctx.filled >= ctx.length) {
                try {
                    ctx.state = DONE;
                    return new String(ctx.data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException("no UTF-8 in the JVM", e);
                }
            } else {
                // need more data
                return null;
            }
        }
    },
    DONE {
        public String decode(ByteBuffer b, DecodeStringContext ctx) {
            throw new ProtocolDecoderException("finished string decoding");
        }
    };

    @Override
    public DecodeStringContext createDecoderState() {
        return new DecodeStringContext();
    }

    @Override
    public void finishDecode(DecodeStringContext ctx) {
        if (ctx.state != DONE) {
            throw new ProtocolDecoderException("unfinished string decoding");
        }
    }
}
