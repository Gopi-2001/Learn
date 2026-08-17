import java.util.*;

public class LocationChecker extends FraudChecker {
    List<String> invalidCountries = Arrays.asList("CountryA", "CountryB"); // Example list of invalid countries

    @Override
    protected boolean canHandleFraud(Order order) {
        return invalidCountries.contains(order.getCountry()); // Example condition for fraud detection
    }

    @Override
    protected void processFraud(Order order) {
        System.out.println(" [LocationChecker]: Fraud detected for order " + order.getId() + " with invalid country");
        // Additional logic to handle the fraud case, e.g., notify authorities, block the user, etc.
    }

}
