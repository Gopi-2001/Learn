public class NotificationService implements ServiceObserver {
    
    interface NotificationStrategy  {
        void sendNotification(Order order);
    }

    static class EmailStrategy implements NotificationStrategy {
        @Override
        public void sendNotification(Order order) {
            // Logic to send email notification
            System.out.println("[Email] " + new java.util.Date() + " | New order: " + order.getItem() + " ($" + order.getAmount() + ")");
        }
    }

    static class SMSStrategy implements NotificationStrategy {
        @Override
        public void sendNotification(Order order) {
            // Logic to send SMS notification
            System.out.println("[SMS] " + new java.util.Date() + " | New order: " + order.getItem() + " ($" + order.getAmount() + ")");
        }
    }

    static class PushStrategy implements NotificationStrategy {
        @Override
        public void sendNotification(Order order) {
            // Logic to send push notification
            System.out.println("[Push] " + new java.util.Date() + " | New order: " + order.getItem() + " ($" + order.getAmount() + ")");
        }
    }

    private FraudChecker fraudChecker;

    public NotificationService() {
        this.fraudChecker = new AmountChecker();
        fraudChecker.setNext(new LocationChecker()).setNext(new FrequencyChecker());
    }
    
    @Override
    public void onOrderPlaced(Order order, NotificationService.NotificationStrategy notificationStrategy) {

        try {
            fraudChecker.handleFraud(order);
        } catch (RuntimeException e) {
            return; // Exit if fraud is detected
        }

        // Send notifications through different channels
        notificationStrategy.sendNotification(order);
    }
    
}
