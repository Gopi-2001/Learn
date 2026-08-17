public class AmountChecker extends FraudChecker {
    @Override
    protected boolean canHandleFraud(Order order) {
        return order.getAmount() > 1000; // Example threshold for fraud detection
    }

    @Override
    protected void processFraud(Order order) {
        System.out.println(" [AmountChecker]: Fraud detected for order " + order.getId() + " with amount " + order.getAmount());
        // Additional logic to handle the fraud case, e.g., notify authorities, block the user, etc.
    }
    
}
