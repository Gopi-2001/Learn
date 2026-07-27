public class Employee {
    private String name;
    private int id;
    private double baseSalary;

    public Employee(String name, int id, double baseSalary) {
        this.name = name;
        this.id = id;
        this.baseSalary = baseSalary;
    }

    public double calculatePay() {
        return this.baseSalary;
    }

    public void displayInfo(){
        System.out.println("======= Info =========");

        System.out.println("Name: " + this.name);
        System.out.println("ID: " + this.id);
        System.out.println("Base Salary: " + this.baseSalary);


    }
}
