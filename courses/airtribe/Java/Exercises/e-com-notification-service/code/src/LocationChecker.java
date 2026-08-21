import java.util.*;

public class LocationChecker extends FraudChecker {
    List<String> invalidCountries = Arrays.asList("CountryA", "CountryB"); // Example list of invalid countries

    @Override
    protected boolean canHandleFraud(Order order) {
        return true; // Example condition for fraud detection
    }

    @Override
    protected void processFraud(Order order) {
        if(invalidCountries.contains(order.getCountry())) {
            System.out.println("[Fraud Check] LocationChecker: BLOCKED — Order#" + order.getId() + " from invalid country: " + order.getCountry());
            throw new RuntimeException("Fraud detected");
            // Additional logic to handle the fraud case, e.g., notify authorities, block the user, etc.
        } else {
            System.out.println("[Fraud Check] LocationChecker: PASSED");
            // Additional logic to handle the fraud case, e.g., notify authorities, block the user, etc.
        }
    }

}
