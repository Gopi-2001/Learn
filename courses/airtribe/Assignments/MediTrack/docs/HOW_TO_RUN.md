# How To Run From CMD

1. Install Correct JDK (26 is used here)and confirm it:
   ```cmd
   java -version
   javac -version
   ```
2. Open Command Prompt in this `MediTrack` folder.
3. Compile:
   ```cmd
   javac -d out -sourcepath src src\com\airtribe\meditrack\Main.java
   ```
4. Run the application:
   ```cmd
   java -cp out com.airtribe.meditrack.Main
   ```
5. Run the manual tests:
   ```cmd
   java -cp out com.airtribe.meditrack.test.TestRunner
   ```
6. Run with sample patient data loaded from `data\patients.csv`:
   ```cmd
   java -cp out com.airtribe.meditrack.Main --loadData
   ```
