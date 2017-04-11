package jfh.jawstestapp1a.Messages;


public abstract class BaseMessage {

    public static final String MSG_TYPE_KEY = "msgType";
    public static final String MSG_VERSION_KEY = "msgVersion";

    private String msgType;
    private String msgVersion;

    public String getMsgType() {
                return msgType;
            }

    public void setMsgType(String msgType) {
                this.msgType = msgType;
            }

    public String getMsgVersion() {
                return msgVersion;
            }

    public void setMsgVersion(String msgVersion) {
                this.msgVersion = msgVersion;
            }
}
