public abstract class FraudChecker {
    private FraudChecker next;

    public FraudChecker setNext(FraudChecker next) {
        this.next = next;
        return next;
    }

    public void handleFraud(Order order) {
        if(canHandleFraud(order)){
            processFraud(order);
        }
        
        if(next != null) {
            next.handleFraud(order);
        } else {
            System.out.println("[Fraud Check] No fraud detected for Order#" + order.getId());
        }
    }

    protected abstract boolean canHandleFraud(Order order);
    protected abstract void processFraud(Order order);

}
