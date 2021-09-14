# Gradle + JavaFX project template

Documentation on howto properly setup a JavaFX project including deployment
with a single fat jar is pretty poor. This project is a template for a standard gradle project,
including JUnit support and a JavaFX application.

Different types of templates can be found in the different branches of this project:

### main:
Basic gradle project including JavaFX and JUnit support

...more branches coming...


## 1. Running the project:
Import a clone/fork of this project as a gradle project in IntelliJ or any other IDE.
Then run the gradle task `.\gradlew.bat run` on Win or `.\gradlew run` on Unix systems.

If you see an empty window popping up, congratulations! JavaFX is running fine. You're good to go.

## 2. Running checks and tests

The gradle task `./gradle test` runs all the JUnit tests.

## 3. Deploying the project by creating a fat jar

### 3.1 Some facts before

JavaFX libraries are - unlike AWT and Swing - not included in the standard JDK since JDK 11.
That means that you *cannot* just build a normal jar of a JavaFX application and expect it to run
whereever you have a Java runtime environment.

You have to somehow provide the JavaFX libraries separately which usually means you have to make your
user download them separately and start the jar with the correct include command line options to 
provide the correct library path which is - let's be honest - not what you want to do when you 
want to share your software.

Another problem is the fact that JavaFX libraries are platform *dependent*. So you need different
ones for Windows, MacOS X or Linux etc.


### 3.2 Create a fat jar "which just runs"

Running the gradle task `javaFxJar` will create a so called fat jar in `build/libs`.

This gradle task analyzes your current operating system and architecture, downloads the correct
JavaFX libraries, compiles your project, packages up the class files of this project and wraps it all up
in a single fat jar file you can just run with a normal compatible JRE. So whoever wants to run this
JavaFX application just has to install a JRE (should be at least Java 11) and then double 
click the jar file (on Windows, for example) or run it with `java -jar NAME_OF_JAR_FILE.jar`

### 3.3 Care

The `javaFxJar` gradle task will create a jar only for the operating system and architecture you are
running it on. If you are developping on a Linux machine and run this task you will get a Linux
compatible jar which won't run on MacOS X or Windows. You will have to run this task on the OS and
architecture you want to build the jar for.
