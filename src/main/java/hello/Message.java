package hello;

public class Message {
    private final int code;
    private final String message;
    private final Object content;

    public Message(int code, String message, Object content){
        this.code = code;
        this.message = message;
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getContent() {
        return content;
    }
}
