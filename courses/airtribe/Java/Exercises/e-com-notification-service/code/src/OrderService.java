import java.util.ArrayList;
import java.util.List;


public class OrderService {
    private Order order;

    private final List<ServiceObserver> observers = new ArrayList<>();

    public void addObserver(ServiceObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ServiceObserver observer) {
        observers.remove(observer);
    }

    public void placeOrder(Order order) {
        this.order = order;
        notifyObservers();
    }

    private void notifyObservers() {
        for (ServiceObserver observer : observers) {
            observer.onOrderPlaced(order);
        }
    }
}
