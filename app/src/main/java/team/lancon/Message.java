package team.lancon;

public class Message {
    private String text;
    private boolean isSentByCurrentUser;

    public Message(String text, boolean isSentByCurrentUser) {
        this.text = text;
        this.isSentByCurrentUser = isSentByCurrentUser;
    }

    public String getText() {
        return text;
    }

    public boolean isSentByCurrentUser() {
        return isSentByCurrentUser;
    }
}