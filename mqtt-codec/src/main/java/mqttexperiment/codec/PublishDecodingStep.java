package mqttexperiment.codec;

import java.nio.ByteBuffer;

import mqttexperiment.codec.msg.PublishMessage;

enum PublishDecodingStep {

    TOPIC_NAME {
        @Override
        public PublishMessage decode(ByteBuffer incoming, PublishDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            String topicName = state.subStrCtxt.state.decode(incoming, state.subStrCtxt);
            if (topicName == null) {
                return null;
            }
            // next step
            state.publish.setTopic(topicName);

            if (state.publish.getQos() == 0) {
                state.st = PAYLOAD;
            } else {
                state.st = MSG_ID_MSB;
            }
            state.consumedByte += 2;
            state.consumedByte += state.subStrCtxt.data.length;

            state.st = PAYLOAD;
            // how many bytes in the payload ?
            byte[] payload = new byte[state.remaining - state.consumedByte];
            state.publish.setPayload(payload);
            return state.st.decode(incoming, state);
        }
    },
    MSG_ID_MSB {
        @Override
        public PublishMessage decode(ByteBuffer incoming, PublishDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            state.publish.setMessageId((incoming.get() & 0xFF) << 8);
            state.st = MSG_ID_LSB;
            return state.st.decode(incoming, state);
        }
    },
    MSG_ID_LSB {
        @Override
        public PublishMessage decode(ByteBuffer incoming, PublishDecoderContext state) {
            if (incoming.remaining() < 1)
                return null;
            state.publish.setMessageId(state.publish.getMessageId() | (incoming.get() & 0xFF));
            state.consumedByte += 2;
            state.st = PAYLOAD;
            // how many bytes in the payload ?
            byte[] payload = new byte[state.remaining - state.consumedByte];
            state.publish.setPayload(payload);
            return state.st.decode(incoming, state);
        }
    },
    PAYLOAD {
        @Override
        public PublishMessage decode(ByteBuffer incoming, PublishDecoderContext state) {
            if (state.consumedByte >= state.remaining) {
                return state.publish;
            }
            
            // how many bytes to decode ?
            while(incoming.remaining() > 0) {
               
               int pos = state.consumedByte - (state.remaining - state.publish.getPayload().length);
               state.publish.getPayload()[pos] = incoming.get();
               state.consumedByte++;
               
               if (state.consumedByte >= state.remaining) {
                   return state.publish;
               }
            }

            // need more bytes
            return null;
        }
    };

    public abstract PublishMessage decode(ByteBuffer incoming, PublishDecoderContext state);
}
