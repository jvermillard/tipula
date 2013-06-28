package mqttexperiment.codec;

import java.nio.ByteBuffer;

import mqttexperiment.codec.msg.AbstractMqttMessage;

import org.apache.mina.codec.ProtocolDecoder;
import org.apache.mina.codec.ProtocolDecoderException;

/**
 * An asynchronous MQTT decoder.
 */
public class MqttDecoder implements ProtocolDecoder<ByteBuffer, AbstractMqttMessage, MqttDecoderContext> {

    @Override
    public MqttDecoderContext createDecoderState() {
        return new MqttDecoderContext();
    }

    @Override
    public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderContext state) {
        return state.st.decode(incoming, state);
    }

    @Override
    public void finishDecode(MqttDecoderContext state) {
        if (state.st != MqttDecodingStep.HEADER) {
            throw new ProtocolDecoderException("A partial message is pending");
        }
    }

}
