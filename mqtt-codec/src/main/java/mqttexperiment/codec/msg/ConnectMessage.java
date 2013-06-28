package mqttexperiment.codec.msg;

import java.nio.ByteBuffer;

public class ConnectMessage extends AbstractMqttMessage {

    private int version;
    
    private int connectFlags;
    
    private int keepAlive;
    
    private String clientId;
    
    private String username;
    
    private String password;
    
    private String willTopic;
    
    private String willMessage;
    
    private String protocolName;
    
    public ConnectMessage( boolean dup, int qos, boolean retain) {
        super(Type.CONNECT, dup, qos, retain);
    }
    
    public ConnectMessage(boolean dup, int qos, boolean retain, int version, int connectFlags, int keelAlive) {
        super(Type.CONNECT, dup, qos, retain);
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
        return (connectFlags & 0x4) != 0;
    }
    
    public boolean isFlagUsername() {
        return (connectFlags & 0x80) != 0;
    }
    
    public boolean isFlagPassword() {
        return (connectFlags & 0x40) != 0;
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

    
    public void setVersion(int version) {
        this.version = version;
    }

    public void setConnectFlags(int connectFlags) {
        this.connectFlags = connectFlags;
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }
    
    public String getProtocolName() {
        return protocolName;
    }

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    @Override
    public String toString() {
        return "ConnectMessage [version=" + version + ", connectFlags=" + connectFlags + ", keepAlive=" + keepAlive
                + ", clientId=" + clientId + ", username=" + username + ", password=" + password + ", willTopic="
                + willTopic + ", willMessage=" + willMessage + ", protocolName=" + protocolName + ", getType()="
                + getType() + ", isDup()=" + isDup() + ", getQos()=" + getQos() + ", isRetain()=" + isRetain() + "]";
    }
    
}