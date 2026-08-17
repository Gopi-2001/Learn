public class main {
    public static void main(String[] args){
        Order order1 = new Order(1001, "Laptop", 99000, "CountryD", 1);
        Order order2 = new Order(1002, "Smartphone", 800, "CountryC", 1);
        Order order3 = new Order(1003, "Tablet", 600, "CountryC", 1);
        Order order4 = new Order(1004, "Headphones", 150, "CountryC", 1);
        Order order5 = new Order(1005, "Keyboard", 100, "CountryA", 1);

        // Create services
        ServiceObserver notificationService = new NotificationService();
        ServiceObserver analyticsService = new AnalyticsService();
        ServiceObserver inventoryService = new InventoryService();

        // OrderService to manage order placement and notify observers
        OrderService orderService = new OrderService();
        orderService.addObserver(inventoryService);
        orderService.addObserver(analyticsService);
        orderService.addObserver(notificationService);

        // Place orders
        System.out.println("====== Placing Order 1 ======");
        orderService.setNotificationStrategy(new NotificationService.EmailStrategy());
        orderService.placeOrder(order1);

        System.out.println("====== Placing Order 2 ======");
        orderService.setNotificationStrategy(new NotificationService.PushStrategy());
        orderService.placeOrder(order2);

        System.out.println("====== Placing Order 3 ======");
        orderService.setNotificationStrategy(new NotificationService.SMSStrategy());
        orderService.placeOrder(order3);

        System.out.println("====== Placing Order 4 ======");
        orderService.setNotificationStrategy(new NotificationService.EmailStrategy());
        orderService.placeOrder(order4);

        System.out.println("====== Placing Order 5 ======");
        orderService.setNotificationStrategy(new NotificationService.PushStrategy());
        orderService.placeOrder(order5);
        
    }
}
