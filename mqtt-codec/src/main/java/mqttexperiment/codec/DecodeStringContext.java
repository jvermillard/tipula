package mqttexperiment.codec;

public class DecodeStringContext {
    // current decoding state
    DecodeString state = DecodeString.LEN_MSB;

    // the length of the string in bytes
    int length = 0;
    
    // the raw string data
    byte[] data;
    
    // how many bytes we read and put in data
    int filled = 0;    
}
