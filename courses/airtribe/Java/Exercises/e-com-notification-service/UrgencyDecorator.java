public class UrgencyDecorator implements BaseMessage {
    private BaseMessage message;

    public UrgencyDecorator(BaseMessage message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return "[URGENT] " + message.getMessage();
    }
}
