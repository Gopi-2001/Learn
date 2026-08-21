public class SignatureDecorator implements BaseMessage {
    private BaseMessage message;

    public SignatureDecorator(BaseMessage message) {
        this.message = message;
    }

    @Override
    public String msg(String message) {
        return message + " [Signed]";
    }
}
