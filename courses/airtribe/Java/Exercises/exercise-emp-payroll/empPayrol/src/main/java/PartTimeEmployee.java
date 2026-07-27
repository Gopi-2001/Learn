public class PartTimeEmployee extends Employee {

    int hoursWorked;
    double hourlyRate;

    public PartTimeEmployee(String name, int id, int hoursWorked, double hourlyRate) {
        super(name,id,0);
        this.hoursWorked = hoursWorked;
        this.hourlyRate = hourlyRate;
    }

    @Override
    public double calculatePay() {
        return hourlyRate*hoursWorked;
    }

    @Override
    public void displayInfo(){
        super.displayInfo();
        System.out.println("Total Pay: " +  this.calculatePay());

        System.out.println("======================");

    }

}
