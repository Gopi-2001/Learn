package car.example.autowire.constructor;

public class Car {
    private Specification specification2;

    public Car(Specification specification2) {
        this.specification2 = specification2;
    }

    public void displayDetails() {
        System.out.println("Car Details: " + specification2.toString());
    }
}
