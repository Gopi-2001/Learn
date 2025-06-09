package car.example.autowire.type;

public class Car {
    private Specification specification2;

    public void setSpecification(Specification specification) {
        this.specification2 = specification;
    }

    public void displayDetails() {
        System.out.println("Car Details: " + specification2.toString());
    }
}
