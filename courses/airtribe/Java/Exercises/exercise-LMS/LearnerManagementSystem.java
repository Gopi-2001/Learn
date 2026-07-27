import java.util.Scanner;

class LearnerManagementSystem {

    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);

        String[] nameArr = new String[10];
        int[] ageArr = new int[10];
        int[] xpArr = new int[10];


        System.out.println("===== Airtribe Learner Manager =====");
        System.out.println("1. Add Learner");
        System.out.println("2. Display All Learners");
        System.out.println("3. Calculate Average XP");
        System.out.println("4. Exit");
    
        int count = 0;

        boolean bool = true;

        while(bool) {

            System.out.println("Input: _ ");
            String inputStr = scanner.nextLine();
            int input = Integer.parseInt(inputStr);

            if(input>4 || input<1) {
                System.out.println("Invalid Input");
            } else {

                switch (input) {
                    case 1: {

                        System.out.println("Please enter your name");
                        
                        String name = scanner.nextLine();

                        System.out.println("Please enter your age");
                        
                        int age = scanner.nextInt();
                        scanner.nextLine(); // Consume the newline character

                        System.out.println("Please enter the XP");
                        int xp = scanner.nextInt();
                        scanner.nextLine(); // Consume the newline character

                        if (age < 18 || age > 100) System.out.println("Please enter age between 18 to 100");

                        nameArr[count] = name;
                        ageArr[count] = age;
                        xpArr[count] = xp;
                        count++;
                        break;
                    }
                    
                    case 2: {

                        System.out.println("--- Learner List ---");

                        for(int i=0; i<count; i++) {
                            System.out.println((i+1) +" Name: " + nameArr[i] + "| Age: " + ageArr[i] + "| XP: " + xpArr[i]);
                        }
                        break;
                    }
                        
                    case 3:
                        {
                        int totalXP = 0;
                        for(int i=0; i<count; i++) {
                            totalXP += xpArr[i];
                        }
                        double averageXP = (double) totalXP / count;
                        System.out.println("Average XP: " + averageXP);
                        break;
                    }
                    case 4:
                        {
                        System.out.println("GoodBye Exiting...");
                        bool = false;
                        break;
                    }
                }        
            }
        }

        
    }
}