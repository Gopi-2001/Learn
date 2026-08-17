public interface ServiceObserver {
    void onOrderPlaced(Order order, NotificationService.NotificationStrategy notificationStrategy);
}
