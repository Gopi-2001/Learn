# Java Multi-Package Build Reference: Manual Compilation vs. Maven

This note explains how a multi-package Java project is compiled and run "by hand" using
only the JDK's command-line tools, and then shows how Maven automates away the pain
points of that manual process.

---

## 1. The Manual (No-Build-Tool) Approach

When you don't use a build tool, you rely directly on `javac` and `java`. Two rules
make this manageable:

1. **Always run your commands from the root source directory** — the folder where your
   top-level package (e.g. `com`) begins.
2. **Always tell the compiler where to put the compiled `.class` files** using the `-d`
   flag, so your source tree stays clean and compiled output is kept separate.

### Example Project Layout

```
learntrack/
├── src/
│   └── main/
│       └── java/               <-- Root Source Directory
│           └── com/
│               └── airtribe/
│                   ├── entity/
│                   │   └── Student.java
│                   ├── service/
│                   │   └── StudentService.java
│                   └── ui/
│                       └── Main.java
└── target/                     <-- Clean destination for compiled files
```

### Step-by-Step Workflow

**Step 1 — Navigate to the project root**

```bash
cd F:\Learn\courses\airtribe\Assignments\LearnTrack\learntrack
```

**Step 2 — Compile everything into `target/`**

Rather than typing the path to every `.java` file individually, generate a file
listing all source files and hand that list to `javac` using the `@` syntax:

```bash
# Generate a text list of all Java files in the project
dir /s /b src\main\java\*.java > sources.txt

# Compile every file on that list into the target directory
javac -d target @sources.txt

# Remove the temporary list once compilation is done
del sources.txt
```

**Step 3 — Run the program**

To execute a compiled class you must supply:
- the **classpath** (`-cp`), pointing at the folder containing the compiled `.class`
  files, and
- the **fully qualified class name** (dot-separated, no `.class` extension).

```bash
java -cp target com.airtribe.learntrack.ui.Main
```

This works fine for a small project, but it starts to break down as the codebase grows.

---

## 2. Why This Manual Approach Doesn't Scale

Three specific problems appear as a project grows in size or team:

| Problem | What Goes Wrong |
|---|---|
| **Interdependency nightmare** | If `Main.java` depends on `StudentService.java`, which depends on `Student.java`, tracking the correct compile order by hand becomes error-prone as the dependency graph grows. |
| **Third-party library trap** | Any external library (e.g. a database driver or mail library) means manually downloading `.jar` files, storing them somewhere, and building an ever-longer `-cp target;lib/mysql-connector.jar;lib/mail.jar` command for every compile/run. |
| **Lack of standardization** | Every developer invents their own folder layout and batch scripts, so a new team member can't just clone a repo and run it — they first have to reverse-engineer someone else's personal conventions. |

This is the exact gap Maven was built to close.

---

## 3. How Maven Solves These Problems

Maven acts as a **project manager** for the build process. Instead of you manually
tracking source order, downloading jars, and remembering long commands, Maven imposes
a standard project structure and drives everything from a single configuration file:
**`pom.xml`** (Project Object Model).

### Manual Pain Point → Maven's Fix

| Manual Workflow Step | How Maven Automates It |
|---|---|
| Hand-crafting folder structures | Enforces a standard layout (`src/main/java`, etc.) across every project |
| Manually tracking which files changed | Detects modified source files and recompiles only what's needed |
| Downloading `.jar` files from websites | Pulls all dependencies automatically from a central repository |
| Long, fragile `-cp` commands | Replaced by short, memorable commands like `mvn compile` or `mvn test` |

### The `pom.xml` File

This lives in the project root and declares the project's identity, Java version,
and dependencies:

```xml
<project xmlns="http://apache.org"
         xmlns:xsi="http://w3.org"
         xsi:schemaLocation="http://apache.org http://apache.org">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.airtribe</groupId>
    <artifactId>learntrack</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>
</project>
```

### Essential Maven Commands

| Command | What It Does |
|---|---|
| `mvn clean` | Deletes the `target/` directory, wiping out any old build output |
| `mvn compile` | Resolves dependencies, works out the correct compile order, and builds `.class` files into `target/classes/` |
| `mvn test` | Compiles the project and runs all unit tests |
| `mvn package` | Compiles the code and bundles it into a single distributable `.jar` |

### Running the App with Maven

Instead of manually building a classpath, Maven can run the app directly:

```bash
mvn exec:java -Dexec.mainClass="com.airtribe.learntrack.ui.Main"
```

---

## 4. Side-by-Side Summary

| Task | Manual (`javac`/`java`) | Maven |
|---|---|---|
| Find all source files | `dir /s /b ... > sources.txt` | Automatic (standard layout) |
| Compile in correct order | You must reason about it yourself | Resolved automatically |
| Add a third-party library | Download jar, extend `-cp` manually | Declare it in `pom.xml`; Maven fetches it |
| Clean old build output | `rmdir /s /q target` | `mvn clean` |
| Compile | `javac -d target @sources.txt` | `mvn compile` |
| Run tests | No built-in mechanism | `mvn test` |
| Package for distribution | Manual zipping/jar creation | `mvn package` |
| Run the app | `java -cp target <FQCN>` | `mvn exec:java -Dexec.mainClass="<FQCN>"` |
| Onboarding a new developer | Must learn your personal folder/script conventions | Clone repo, run `mvn compile` — structure is standardized |

**Bottom line:** the manual approach is transparent and useful for understanding what
the JVM is actually doing under the hood (compiling source into `.class` files and
running them off a classpath), but it doesn't scale past a small, single-developer
project. Maven takes over dependency management, build ordering, testing, and
packaging behind a small set of standardized commands, which is why virtually all
real-world Java projects use it (or an equivalent like Gradle) instead of raw
`javac`/`java`.