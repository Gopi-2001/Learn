public class FullTimeEmployee extends Employee {

    double bonus;

    public FullTimeEmployee(String name, int id, double baseSalary, double bonus) {
        super(name, id, baseSalary);
        this.bonus = bonus;
    }

    @Override
    public double calculatePay() {
        return super.calculatePay() + bonus;
    }

    @Override
    public void displayInfo(){
        super.displayInfo();

        System.out.println("Total Pay: " +  this.calculatePay());

        System.out.println("Bonus: " +  this.bonus);


    }

    public void displayInfo(boolean showBonus) {

        super.displayInfo();

        System.out.println("Total Pay: " +  this.calculatePay());

        if(showBonus){
            System.out.println("Bonus: " +  this.bonus);
        }


    }
}
