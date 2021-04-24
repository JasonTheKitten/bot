package everyos.bot.chat4j.status;

public class Status {
    private StatusType type;
    private String text;
    
    public Status(StatusType type, String text) {
        this.type = type;
        this.text = text;
    }

    public StatusType getType() {
        return type;
    }

    public String getText() {
        return text;
    }
}