public class NotificationService implements ServiceObserver {


    interface NotificationChannel {
        void sendNotification(Order order);
    }

    static class EmailChannel implements NotificationChannel {
        @Override
        public void sendNotification(Order order) {
            // Logic to send email notification
            System.out.println(" [EmailChannel]: Sending email notification for order " + order.getId());
        }
    }

    static class SMSChannel implements NotificationChannel {
        @Override
        public void sendNotification(Order order) {
            // Logic to send SMS notification
            System.out.println(" [SMSChannel]: Sending SMS notification for order " + order.getId());
        }
    }

    static class PushChannel implements NotificationChannel {
        @Override
        public void sendNotification(Order order) {
            // Logic to send push notification
            System.out.println(" [PushChannel]: Sending push notification for order " + order.getId());
        }
    }



    @Override
    public void onOrderPlaced(Order order) {
        // Logic to send notification based on the order
        System.out.println(" [NotificationService]: Sending notification for order " + order.getId());
    }
    
}
