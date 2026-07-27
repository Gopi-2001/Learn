public class EmployeeMain {

    public static void main(String[] args) {
        FullTimeEmployee fullTimeEmployee = new FullTimeEmployee("Anu",1,120000,10000);

        PartTimeEmployee partTimeEmployee = new PartTimeEmployee("Kunal",2,5,200);

        Employee[] employeesArr = new  Employee[] {fullTimeEmployee,partTimeEmployee};

        for(int employeeIndex = 0; employeeIndex < employeesArr.length; employeeIndex++){
            employeesArr[employeeIndex].displayInfo();

            System.out.println();
        }

        fullTimeEmployee.displayInfo(false);
    }
}
