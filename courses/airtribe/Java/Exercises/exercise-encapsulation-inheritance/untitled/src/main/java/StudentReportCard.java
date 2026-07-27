public class StudentReportCard {
    private String name;
    private int rollNumber;
    private double mathScore;
    private double scienceScore;
    private double englishScore;

    StudentReportCard(String name,int rollNumber) {
        this.name = name;
        this.rollNumber = rollNumber;
    }

    public String getName() {
        return name;
    }

    public int getRollNumber() {
        return rollNumber;
    }

    public double getMathScore() {
        return mathScore;
    }

    public double getScienceScore() {
        return scienceScore;
    }

    public double getEnglishScore() {
        return englishScore;
    }

    public void setMathScore(double mathScore) {
        if(mathScore>=0 && mathScore<=100) {
            this.mathScore = mathScore;
        } else {
            System.out.println("Invalid math score");
        }
    }

    public void setScienceScore(double scienceScore) {
        if(scienceScore>=0 && scienceScore<=100) {
            this.scienceScore = scienceScore;
        } else {
            System.out.println("Invalid science score");
        }
    }


    public void setEnglishScore(double englishScore) {
        if(englishScore>=0 && englishScore<=100) {
            this.englishScore = englishScore;
        } else {
            System.out.println("Invalid english score");
        }
    }

    public double getPercentage() {
        double totalScore = englishScore + mathScore + scienceScore;

        double percentage = (totalScore / 300) * 100;

        return percentage;
    }

    public String getGrade() {

        double percentage = getPercentage();

        if(percentage >= 80) {
            return "A";
        } else if(percentage >= 60) {
            return "B";
        } else if(percentage >= 40) {
            return "C";
        } else {
            return "F";
        }

    }

    public void printReportCard(){
        System.out.println("=== Report Card ===");

        System.out.println("Name: " + name);

        System.out.println("Roll Number: " + rollNumber);

        System.out.println("Math Score: " + mathScore);

        System.out.println("Science Score: " + scienceScore);

        System.out.println("English Score: " + englishScore);

        String formattedPercentage = String.format("%.2f", getPercentage());
        System.out.println("Percentage: " + formattedPercentage + "%");

        System.out.println("Grade: " + getGrade());

        System.out.println("===================");
    }

    public static void main(String[] args) {

        StudentReportCard studentReportCard = new StudentReportCard("Rahul",101);

        studentReportCard.setEnglishScore(90.0);

        studentReportCard.setMathScore(85.0);

        studentReportCard.setScienceScore(72.0);

        studentReportCard.printReportCard();
    }



}
