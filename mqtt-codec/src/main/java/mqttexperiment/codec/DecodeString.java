package mqttexperiment.codec;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.apache.mina.codec.ProtocolDecoder;
import org.apache.mina.codec.ProtocolDecoderException;

public enum DecodeString implements ProtocolDecoder<ByteBuffer, String, DecodeStringContext> {

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
            while(b.remaining() > 0 && ctx.filled < ctx.length) {
                ctx.data[ctx.filled++] = b.get();
            }
            if (ctx.filled >= ctx.length) {
                try {
                    ctx.state = DONE;
                    return new String(ctx.data,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException("no UTF-8 in the JVM",e);
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
        if ( ctx.state != DONE) {
            throw new ProtocolDecoderException("unfinished string decoding");
        }
    }
}
