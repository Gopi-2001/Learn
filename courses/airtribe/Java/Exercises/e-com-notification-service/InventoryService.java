public class InventoryService implements ServiceObserver {
    @Override
    public void onOrderPlaced(Order order) {
        // Logic to update inventory based on the order
        System.out.println("InventoryService: Updating inventory for order " + order.getId());
    }
    
}
