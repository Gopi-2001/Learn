import java.util.ArrayList;
import java.util.List;

/**
 * SOLUTION: E-Commerce Notification System
 * 
 * Combines all four Behavioral patterns
 *  Observer  → OrderService notifies listeners
 *  Strategy  → NotificationChannel(Email/SMS/Push)
 *  Chain     → FraudChecker pipeline
 *  Decorator → Message formatting layers
 * 
*/
public class Main {

    /** ________________________________________________
        ORDER (domain object)
        ________________________________________________
    */

    /** 
     * Class Order - id, amount, userId, country
     * Interface Listener - listen(order) -> Notification, Analytics, Inventory
    */

    static class Order {
        final String id, item, country, userId;
        final double amount;

        Order(String id, String item, String country, String userId, double amount){
            this.id = id;
            this.item = item;
            this.country = country;
            this.userId = userId;
            this.amount = amount;
        }

        public String toString() {
            return "Order#" + id + ": " + item + " ($" + String.format("%.2f",amount) + ")";
        }
    }

    /** ________________________________________________
        OBSERVER - OrderService is the subject
        ________________________________________________
    */
    interface OrderListener {
        void onOrderPlaced(Order order);
    }

    static class OrderService {
        private final List<OrderListener> listeners = new ArrayList<>();

        void addListener(OrderListener orderListener) {
            listeners.add(orderListener);
        }

        void placeOrder(Order order){
            System.out.println(" [OrderService] New order placed: " + order);

            notifyListener(order);
        }

        void notifyListener(Order order){
            for(OrderListener l : listeners){
                l.onOrderPlaced(order);
            }
        }
    }

    /** ________________________________________________
        STRATEGY - Notification channels
        ________________________________________________
    */
    interface NotificationChannel {
        void send(String message);
    }

    static class EmailChannel implements NotificationChannel {
        @Override
        public void send(String message){
            System.out.println(" [EMAIL] " + message);
        }
    }

    static class SMSChannel implements NotificationChannel {
        @Override
        public void send(String message){
            System.out.println(" [SMS] " + message);
        }
    }

    static class PushChannel implements NotificationChannel {
        @Override
        public void send(String message){
            System.out.println(" [Push] " + message);
        }
    }
    
    /** ________________________________________________
        CHAIN OF RESPONSIBILITY - Fraud Checks
        ________________________________________________
    */

    static abstract class FraudChecker {
        private FraudChecker next;

        FraudChecker setNext(FraudChecker next){
            this.next = next;
            return next;
        }

        boolean check(Order order){
            if(!passes(order)) return false;
            if(next != null) return next.check(order);
            return true;
        }

        abstract boolean passes(Order order);
    }

    static class AmountChecker extends FraudChecker {
        @Override
        boolean passes(Order order){
            if(order.amount > 10000){
                System.out.println(" [Fraud] AmountChecker: BLOCKED - $"
                    + String.format("%.2f",order.amount) + " exceeds limit");
                
                return false;
            }
            System.out.println(" [Fraud] AmountChecker: PASSED");
            return true;
        }
    }

    static class LocationChecker extends FraudChecker {
        private final List<String> blackList = List.of("NK","SY");

        @Override
        boolean passes(Order order){
            if(blackList.contains(order.country)){
                System.out.println(" [Fraud] LocationChecker: BLOCKED - Country " + order.country);
                return false;
            }
            System.out.println(" [Fraud] LocationChecker: PASSED");
            return true;
        }
    }

    static class FrequencyChecker extends FraudChecker {
        @Override
        boolean passes(Order order){
            System.out.println(" [Fraud] FrequencyChecker: PASSED");
            return true;
        }
    }
     

    /** ________________________________________________
        DECORATOR - Message Formatting
        ________________________________________________
    */
    interface Message {
        String getContent();
    }

    static class BaseMessage implements Message {
        private final Order order;

        BaseMessage(Order order){
            this.order = order;
        }
        @Override
        public String getContent() {
            return "New Order: " + order.item + " ($" + String.format("%.2f",order.amount) + ")";
        }
    }

    static abstract class MessageDecorator implements Message {
        protected final Message wrapped;

        MessageDecorator(Message wrapped){
            this.wrapped = wrapped;
        }
    }

    static class UrgencyDecorator extends MessageDecorator {
        private final double threshold;

        UrgencyDecorator(Message msg, double threshold){
            super(msg);
            this.threshold = threshold;
        }

        public String getContent(){
            return "[URGENT] " + wrapped.getContent();
        }
    }

    static class TimestampDecorator extends MessageDecorator {
        TimestampDecorator(Message msg){
            super(msg);
        }

        @Override
        public String getContent() {
            return "2024-01-15 10:30 | " + wrapped.getContent();
        }
    }

    static class SignatureDecorator extends MessageDecorator {
            SignatureDecorator(Message msg) { super(msg); }

            public String getContent(){
                return wrapped.getContent() + " - Mystore Inc.";
            }
    }

    /** ________________________________________________
        OBSERVER IMPLEMENTATIONS - wire everything 
        together
        ________________________________________________
    */
    static class InventoryService implements OrderListener {
        @Override
        public void onOrderPlaced(Order order) {
            System.out.println(" [Analytics] Reserve stock for " + order);
        }
    }

    static class AnalyticsService implements OrderListener {
        @Override
        public void onOrderPlaced(Order order) {
            System.out.println(" [Analytics] Tracking " + order);
        }
    }

    // NotificationService: Observer + uses Strategy + chain + Decorator
    static class NotificationService implements OrderListener {
        private NotificationChannel notificationChannel;
        private final FraudChecker fraudChain;

        NotificationService(NotificationChannel notificationChannel, FraudChecker fraudChain) {
            this.notificationChannel = notificationChannel;
            this.fraudChain = fraudChain;
        }

        void setChannel(NotificationChannel channel){
            this.notificationChannel = channel;
        }

        @Override
        public void onOrderPlaced(Order order) {
            // Chain: fraud check
            if(!fraudChain.check(order)) return;

            // Decorator: build formatted message
            Message msg = new BaseMessage(order);
            msg = new TimestampDecorator(msg);
            if(order.amount > 5000) msg = new UrgencyDecorator(msg, 5000);

            // Strategy: send via chosen channel
            notificationChannel.send(msg.getContent());
        }
    }

    public static void main(String[] args){
        System.out.println("=== E-Commerice Notification System ===\n");
        
        //Build fraud check chain
        FraudChecker amountChk = new AmountChecker();
        amountChk.setNext(new LocationChecker()).setNext(new FrequencyChecker());

        //Create Services (Observers)
        InventoryService inventory = new InventoryService();
        AnalyticsService analytics = new AnalyticsService();
        NotificationService notifications = new NotificationService(new EmailChannel(), amountChk);

        //Register observers with OrderService
        OrderService orderService = new OrderService();
        orderService.addListener(inventory);
        orderService.addListener(analytics);
        orderService.addListener(notifications);

        // --- Order 1 : Normal Order, Email ---
        System.out.println("\n-- Order 1: Normal Order (Email) ---");
        orderService.placeOrder(new Order("1001","Laptop","US","User1",999.00));

        // --- Order 2 : High-Value -> Blocked By Fraud ---
        System.out.println("\n-- Order 2: High-value Order (Blocked) ---");
        orderService.placeOrder(new Order("1002","Server Rack","US","User2", 15000.00));

        // --- Order 3 : Switch to SMS
        System.out.println("\n--- Order 3: Switch to SMS ---");
        notifications.setChannel(new SMSChannel());
        orderService.placeOrder(new Order("1003","Keyboard","US","User3",79.00));

    }

}