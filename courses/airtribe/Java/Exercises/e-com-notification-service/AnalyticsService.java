public class AnalyticsService implements ServiceObserver {
    @Override
    public void onOrderPlaced(Order order) {
        // Logic to update analytics based on the order
        System.out.println(" [AnalyticsService]: Updating analytics for order " + order.getId());
    }
}
