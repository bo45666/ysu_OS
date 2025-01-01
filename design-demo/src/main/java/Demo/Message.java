package Demo;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/12/31 10:36
 */
public class Message {
    private MessageType type;
    private String text;
    public Message(MessageType type, String text) {
        this.type = type;
        this.text = text;
    }
    public MessageType getType() {
        return type;
    }
    public String getText() {
        return text;
    }
}
