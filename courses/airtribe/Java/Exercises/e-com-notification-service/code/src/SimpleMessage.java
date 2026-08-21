public class SimpleMessage implements BaseMessage {
    String message;

    @Override
    public String msg(String message) {
        return this.message;
    }
}
