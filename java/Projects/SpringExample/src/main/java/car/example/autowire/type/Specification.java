package car.example.autowire.type;

public class Specification {
    private String make;
    private String model;

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        System.out.println("Setter Called");
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "Specification{" +
                "model='" + model + '\'' +
                ", make='" + make + '\'' +
                '}';
    }
}
