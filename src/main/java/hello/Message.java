package hello;

public class Message {
    private final int code;
    private final String message;
    private final String content;

    public Message(int code, String message, String content){
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

    public String getContent() {
        return content;
    }
}
