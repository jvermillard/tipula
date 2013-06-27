package mqttexperiment.codec.msg;

import java.nio.ByteBuffer;

public class ConnectMessage extends AbstractMqttMessage {

    private final int version;
    
    private final int connectFlags;
    
    private final int keepAlive;
    
    private String clientId;
    
    private String username;
    
    private String password;
    
    private String willTopic;
    
    private String willMessage;
    
    public ConnectMessage(Type type, boolean dup, int qos, boolean retain, int version, int connectFlags, int keelAlive) {
        super(type, dup, qos, retain);
        this.version = version;
        this.connectFlags = connectFlags;
        this.keepAlive = keelAlive;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteBuffer visit(MqttMessageEncodingVisitor visitor) {
        return visitor.visit(this);
    }
    
    public boolean isFlagWill() {
        return (connectFlags & 0b0000_0100) != 0;
    }
    
    public boolean isFlagUsername() {
        return (connectFlags & 0b1000_0000) != 0;
    }
    
    public boolean isFlagPassword() {
        return (connectFlags & 0b0100_0000) != 0;
    }
    
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWillTopic() {
        return willTopic;
    }

    public void setWillTopic(String willTopic) {
        this.willTopic = willTopic;
    }

    public String getWillMessage() {
        return willMessage;
    }

    public void setWillMessage(String willMessage) {
        this.willMessage = willMessage;
    }

    public int getVersion() {
        return version;
    }

    public int getConnectFlags() {
        return connectFlags;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ConnectMessage [version=" + version + ", connectFlags=" + connectFlags + ", keepAlive=" + keepAlive
                + ", clientId=" + clientId + ", username=" + username + ", password=" + password + ", willTopic="
                + willTopic + ", willMessage=" + willMessage + ", getType()=" + getType() + ", isDup()=" + isDup()
                + ", getQos()=" + getQos() + "]";
    }
}