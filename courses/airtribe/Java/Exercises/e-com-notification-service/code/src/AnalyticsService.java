public class AnalyticsService implements ServiceObserver {
    @Override
    public void onOrderPlaced(Order order, NotificationService.NotificationStrategy notificationStrategy) {
        // Logic to update analytics based on the order
        System.out.println("[Analytics] Tracking Order#"  + order.getId() + ": " + order.getItem() + "($" + order.getAmount() + ")");
    }
}
