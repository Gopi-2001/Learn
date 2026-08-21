import java.util.*;

public class FrequencyChecker extends FraudChecker {
    private static final int MAX_ORDERS_PER_HOUR = 1; // Example threshold for frequency check
    private Map<Integer, List<Long>> orderTimestamps = new HashMap<>(); // Store order timestamps for each user

    @Override
    protected boolean canHandleFraud(Order order) {
        return true;
    }

    protected boolean isOrderFrequencyExceeded(Order order) {
        int userId = order.getUserId();
        long currentTime = System.currentTimeMillis();

        // Get the list of timestamps for the user, or create a new list if none exists
        List<Long> timestamps = orderTimestamps.getOrDefault(userId, new ArrayList<>());

        // Remove timestamps that are older than 1 hour
        timestamps.removeIf(timestamp -> (currentTime - timestamp) > 3600000); // 3600000 ms = 1 hour

        // Check if the number of orders in the last hour exceeds the limit
        if (timestamps.size() >= MAX_ORDERS_PER_HOUR) {
            return true; // Frequency exceeded
        }

        // Add the current order's timestamp to the list and update the map
        timestamps.add(currentTime);
        orderTimestamps.put(userId, timestamps);

        return false; // Frequency not exceeded
    }

    @Override
    protected void processFraud(Order order) {
        if(isOrderFrequencyExceeded(order)) {
            System.out.println("[Fraud Check] FrequencyChecker: BLOCKED — User#" + order.getUserId() + " has placed too many orders in the last hour");

            throw new RuntimeException("Fraud detected");
            // Additional logic to handle the fraud case, e.g., notify authorities, block the user, etc.
        } else {
            System.out.println("[Fraud Check] FrequencyChecker: PASSED");
            
            // Additional logic to handle the fraud case, e.g., notify authorities, block the user, etc.
        }
    }
    
}
