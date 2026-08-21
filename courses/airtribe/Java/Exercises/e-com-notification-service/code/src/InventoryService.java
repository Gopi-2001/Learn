public class InventoryService implements ServiceObserver {
    @Override
    public void onOrderPlaced(Order order, NotificationService.NotificationStrategy notificationStrategy) {
        // Logic to update inventory based on the order
        System.out.println("[Inventory] Reserving stock for Order#" + order.getId() + ": " + order.getItem() + "($" + order.getAmount() + ")");
    }
    
}
