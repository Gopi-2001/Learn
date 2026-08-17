public abstract class FraudChecker {
    private FraudChecker next;

    public FraudChecker setNext(FraudChecker next) {
        this.next = next;
        return next;
    }

    public void handleFraud(Order order) {
        if(canHandleFraud(order)){
            processFraud(order);
        } else if(next != null) {
            next.handleFraud(order);
        }
    }

    protected abstract boolean canHandleFraud(Order order);
    protected abstract void processFraud(Order order);

}
