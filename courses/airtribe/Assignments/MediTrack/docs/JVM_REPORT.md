# JVM Report

Java source is compiled by `javac` into bytecode (`.class` files). That bytecode runs on a JVM for the current operating system, which supports "Write Once, Run Anywhere" when a compatible JVM exists.

## Class Loader

The class loader finds `.class` files, loads them into memory, links them, and initializes static fields and static blocks. This project loads `Main` first, then loads classes it uses such as `PatientService`.

## Runtime Data Areas

- **Heap:** stores objects such as `Patient`, `Doctor`, and `ArrayList` instances. It is shared by threads.
- **Stack:** each method call gets a stack frame containing local variables and method state.
- **Method Area / Metaspace:** keeps class metadata, method definitions, and static values.
- **PC Register:** every thread tracks the bytecode instruction it is currently executing.

## Execution Engine

The execution engine runs Java bytecode after classes have been loaded into memory.

## JIT Compiler vs Interpreter

The **interpreter** reads and runs bytecode one instruction at a time. It can start running a program quickly, but repeatedly interpreting the same code can be slower.

The **JIT (Just-In-Time) compiler** watches for code that runs often, such as a loop or frequently called method. It compiles that bytecode into native machine code while the program is running. This makes repeated execution faster.

In simple terms: the interpreter helps the application start, while the JIT compiler improves performance for frequently used code.

## Write Once, Run Anywhere

Java source code is compiled once into platform-independent bytecode. The same `.class` file can run on Windows, Linux, or macOS when that system has a compatible JVM.

The JVM is different for each operating system because it translates bytecode into instructions for that computer. This is why Java programs do not need to be rewritten separately for each operating system.
