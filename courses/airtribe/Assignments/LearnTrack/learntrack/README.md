# Project description

Learn Track is a learning tracking application. It can be used to manage Student, Course and Student Enrollment.

# Learn Track Diagram

![learntrack-uml-diagram.png](src\main\java\com\airtribe\learntrack\docs\images\learntrack-uml-diagram.png)

## How to compile and run

**Compile everything into `target/`**

```bash
# Navigate to the project root
cd {$PWD}\LearnTrack\learntrack

# Generate a text list of all Java files in the project
dir /s /b src\main\java\*.java > sources.txt

# Compile every file on that list into the target directory
javac -d target @sources.txt

# Remove the temporary list once compilation is done
del sources.txt
```

**Run the program**

```bash
java -cp target com.airtribe.learntrack.ui.main
```