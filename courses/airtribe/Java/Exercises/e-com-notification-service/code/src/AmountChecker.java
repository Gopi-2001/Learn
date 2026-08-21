public class AmountChecker extends FraudChecker {
    @Override
    protected boolean canHandleFraud(Order order) {
        return true; // Example threshold for fraud detection
    }

    @Override
    protected void processFraud(Order order) {
        if(order.getAmount() > 1000) {
            System.out.println("[Fraud Check] AmountChecker: BLOCKED — Amount $" + order.getAmount() + " exceeds limit");
            throw new RuntimeException("Fraud detected");
            // Additional logic to handle the fraud case, e.g., notify authorities, block the user, etc.
        } else {
            System.out.println("[Fraud Check] AmountChecker: PASSED");
            // Additional logic to handle the fraud case, e.g., notify authorities, block the user, etc.
        }
    }
    
}
