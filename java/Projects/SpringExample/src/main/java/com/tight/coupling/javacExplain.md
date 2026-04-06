# Java CLI Execution Guide: Handling Packages & Dependencies

This document explains how to manually compile and run Java programs that use `package` declarations, and why IDEs like VS Code or IntelliJ make this process look simpler.

---

## 📂 Q1. Why do we have to follow these steps?

In Java, a class’s **Fully Qualified Name (FQN)** includes its package. For your project, the class name isn't just `TightCouplingExample`; it is `com.tight.coupling.TightCouplingExample`.

The Java Virtual Machine (JVM) enforces a strict rule: **The directory structure must match the package name.**
*   **The Problem:** If you are *inside* the `coupling` folder, the JVM cannot find the "start" of the package (`com`).
*   **The Requirement:** You must run the command from the **Source Root** (the folder containing `com`) so the JVM can follow the folder path `com -> tight -> coupling` to find your class.

---

## 🛠️ Q2. Why can't I just do simple `javac` and `java` commands?

### The Compilation Issue (`javac`)
When classes are separated into different files (e.g., `TightCouplingExample.java` and `UserManager.java`), the compiler needs to see **all** of them to resolve symbols. Running `javac File.java` only looks at that one file.
*   **Fix:** Use `javac *.java` to compile all related files in the folder at once.

### The Execution Issue (`java`)
If you run `java TightCouplingExample` while inside the `coupling` folder, you get a `NoClassDefFoundError (wrong name)`.
*   **The Cause:** The JVM opens the file and sees `package com.tight.coupling;` at the top. It then expects the file to be located in a sub-folder path `./com/tight/coupling/`. Since you are already *in* that folder, the path doesn't match its internal package definition.

---

## 💻 Q3. Why does it work in VS Code or IntelliJ?

IDEs automate the "Plumbing" of Java behind the scenes:
1.  **Source Roots:** The IDE automatically identifies `src/main/java` as the starting point for all packages.
2.  **Classpath Management:** When you click "Run," the IDE executes a long command that sets the `-classpath` to your build folder and uses the **Full Package Name** automatically.
3.  **Build Folders:** IDEs compile `.class` files into a separate `bin/` or `target/` folder while maintaining the folder structure for you.

---

## 🚀 Step-by-Step Manual Execution

To run your program manually from the terminal, follow these exact steps:

### 1. Navigate to the Source Root
Go to the folder where your package structure starts (usually the `java` folder).
```powershell
cd F:\Learn\java\Projects\SpringExample\src\main\java\
```

### 2. Compile All Files
Compile the classes by referencing their path from the root.

```powershell
javac com/tight/coupling/*.java
```
### 3. Run using the Full Name
Run the program using dots . to represent the package levels. Do not add .class at the end.

```powershell
java com.tight.coupling.TightCouplingExample
```

| Action | Command | Required Location |
| :--- | :--- | :--- |
| **Compile** | `javac com/tight/coupling/*.java` | Source Root (`...\java`) |
| **Run** | `java com.tight.coupling.MainClass` | Source Root (`...\java`) |
| **IDE Run** | Click "Play" Button | Anywhere (IDE handles it) |


