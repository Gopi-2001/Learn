import java.util.*;

public class FrequencyChecker extends FraudChecker {
    private static final int MAX_ORDERS_PER_HOUR = 5; // Example threshold for frequency check
    private Map<Integer, List<Long>> orderTimestamps = new HashMap<>(); // Store order timestamps for each user

    @Override
    protected boolean canHandleFraud(Order order) {
        int userId = order.getUserId();
        long currentTime = System.currentTimeMillis();

        // Get the list of timestamps for the user, or create a new list if none exists
        List<Long> timestamps = orderTimestamps.getOrDefault(userId, new ArrayList<>());

        // Remove timestamps that are older than 1 hour
        timestamps.removeIf(timestamp -> (currentTime - timestamp) > 3600000); // 1 hour in milliseconds

        // Check if the number of orders in the last hour exceeds the threshold
        if (timestamps.size() >= MAX_ORDERS_PER_HOUR) {
            return true; // Fraud detected due to high frequency of orders
        }

        // Add the current timestamp to the list and update the map
        timestamps.add(currentTime);
        orderTimestamps.put(userId, timestamps);

        return false; // No fraud detected based on frequency
    }

    @Override
    protected void processFraud(Order order) {
        System.out.println(" [FrequencyChecker]: Fraud detected for order " + order.getId() + " due to high frequency of orders");
        // Additional logic to handle the fraud case, e.g., notify authorities, block the user, etc.
    }
    
}
