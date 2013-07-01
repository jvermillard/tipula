package mqttexperiment.codec;

import java.nio.ByteBuffer;

import mqttexperiment.codec.msg.AbstractMqttMessage;

import org.junit.Assert;
import org.junit.Test;

public class SubscribeDecodeTest {

    private byte[] data = hexStringToByteArray("820900010004626c656800");

    //private byte[] data = hexStringToByteArray("820E0000000341424300000344454601");
    @Test
    public void decode_subscribe_message() {

        MqttDecoder dec = new MqttDecoder();
        MqttDecoderContext state = dec.createDecoderState();

        System.err.println(dec.decode(ByteBuffer.wrap(data), state));
    }

    @Test
    public void decode_byte_by_byte_subscribe_message() {
        MqttDecoder dec = new MqttDecoder();
        MqttDecoderContext state = dec.createDecoderState();

        AbstractMqttMessage msg;
        int index = 0;
        do {
            byte[] chunk = new byte[1];
            chunk[0] = data[index++];
            msg = dec.decode(ByteBuffer.wrap(chunk), state);
            if (msg != null) {
                System.err.println(msg);
                break;
            }
        } while (index < data.length);

        System.err.println(state.st.name());
        Assert.assertNotNull(msg);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
