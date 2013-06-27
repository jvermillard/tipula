package mqttexperiment.codec;

import java.nio.ByteBuffer;

import mqttexperiment.codec.msg.AbstractMqttMessage;

import org.apache.mina.codec.ProtocolDecoder;
import org.apache.mina.codec.ProtocolDecoderException;

/**
 * An asynchronous MQTT decoder.
 */
public class MqttDecoder implements ProtocolDecoder<ByteBuffer, AbstractMqttMessage, MqttDecoderState> {

    @Override
    public MqttDecoderState createDecoderState() {
        return new MqttDecoderState();
    }

    @Override
    public AbstractMqttMessage decode(ByteBuffer incoming, MqttDecoderState state) {
        return state.st.decode(incoming, state);
    }

    @Override
    public void finishDecode(MqttDecoderState state) {
        if (state.st != MqttDecodingStep.HEADER) {
            throw new ProtocolDecoderException("A partial message is pending");
        }
    }

}
