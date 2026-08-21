public class TimestampDecorator implements BaseMessage {
    private BaseMessage message;

    public TimestampDecorator(BaseMessage message) {
        this.message = message;
    }

    @Override
    public String msg(String message) {
        long timestamp = System.currentTimeMillis();
        return "[" + timestamp + "] " + message;
    }
    
}
