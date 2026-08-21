# Compiling Java Code with javac

## Basic Syntax
```bash
javac FileName.java
```

## Common Options

| Option | Purpose | Example |
|--------|---------|---------|
| `-d` | Specify output directory for .class files | `javac -d target/classes Main.java` |
| `-cp` or `-classpath` | Specify classpath for dependencies | `javac -cp lib/* Main.java` |
| `-sourcepath` | Specify source file directory | `javac -sourcepath src Main.java` |
| `-encoding` | Specify file encoding (e.g., UTF-8) | `javac -encoding UTF-8 Main.java` |
| `-verbose` | Show compilation details | `javac -verbose Main.java` |

## Common Scenarios

### 1. Compile Single File
```bash
javac Main.java
# Produces: Main.class (in same directory)
```

### 2. Compile All Java Files in a Directory
```bash
javac src/*.java
# Compiles all .java files in src/ folder
```

### 3. Compile to Specific Output Directory
```bash
javac -d target/classes src/*.java
# All .class files go to target/classes/
```

### 4. Compile with External Libraries
```bash
javac -cp lib/library.jar -d target/classes src/*.java
# Include dependencies during compilation
```

### 5. Compile Multiple Source Directories
```bash
javac -sourcepath src:lib -d target/classes src/*.java
```

## Running Compiled Code

Once compiled, run with `java`:
```bash
java -cp target/classes ClassName
# Note: Use class name, NOT filename (no .class extension)
```

## For Your Project
```bash
# Create output directory
mkdir target\classes

# Compile all files
javac -d target/classes code/src/*.java

# Run main class
java -cp target/classes main
```

## Common Compilation Errors

| Error | Cause | Fix |
|-------|-------|-----|
| "cannot find symbol" | Missing import or undefined variable | Check spelling, add import |
| "reached end of file while parsing" | Missing closing brace `}` | Add missing braces |
| "package does not exist" | Wrong package declaration or path | Remove incorrect package or restructure folders |

