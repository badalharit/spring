# Explanation of terminal commands (from terminal.log)

This file explains the purpose of each command that was executed to create and build the classic **Core Spring MVC (WAR)** project skeleton.

---

## 1) Check Java runtime
### Command
```bash
cd c:/wamp64/www/spring && java -version
```
### Purpose
- Ensures Java is installed and prints the Java version.
- Maven needs a working Java runtime (and ideally a JDK) to compile projects.

---

## 2) Check Maven installation
### Command
```bash
cd c:/wamp64/www/spring && mvn -v
```
### Purpose
- Confirms Maven is installed.
- Prints the Maven version, Maven home, and the Java version Maven will use.

---

## 3) Generate a new WAR webapp project skeleton
### Command
```bash
cd c:/wamp64/www/spring && mvn -q -DskipTests archetype:generate -DgroupId=com.example -DartifactId=core-spring-mvc -DarchetypeArtifactId=maven-archetype-webapp -DinteractiveMode=false
```
### Purpose
- Uses Maven’s built-in **archetype** system to create a new project.
- `maven-archetype-webapp` creates a basic **WAR** structure.
- Flags:
  - `-DartifactId=core-spring-mvc`: name of the generated folder
  - `-DinteractiveMode=false`: non-interactive generation
  - `-DskipTests`: avoids tests during generation
  - `-q`: quiet mode (less output)

---

## 4) Move a parent pom into the created project (cleanup)
### Command
```bash
cd c:/wamp64/www/spring && cd core-spring-mvc && move ..\pom.xml . 2>nul || true
```
### Purpose
- Attempts to move a `pom.xml` generated one directory above into the project folder.
- `2>nul` hides errors if the source file doesn’t exist.
- `|| true` prevents the overall command chain from failing.

---

## 5) Verify the project folder exists
### Command
```bash
cd c:/wamp64/www/spring && dir
```
### Purpose
- Lists `c:/wamp64/www/spring` to confirm `core-spring-mvc/` was created.

---

## 6) Build the WAR
### Command
```bash
cd c:/wamp64/www/spring/core-spring-mvc && mvn package -DskipTests
```
### Purpose
- Compiles (if sources exist), processes resources, and packages the app into a `.war`.
- With your current skeleton it reported **`No sources to compile`**, but it still assembled a WAR from `src/main/webapp`.

### Key lines from the log
- `Packaging webapp`
- `Copying webapp resources`
- `Building war: ...\target\core-spring-mvc`
- `BUILD SUCCESS`

---

## 7) Verify the build artifact exists
### Command
```bash
cd c:/wamp64/www/spring/core-spring-mvc && dir target
```
### Purpose
- Lists the `target/` directory.
- Confirms the WAR exists (expected):
  - `core-spring-mvc.war`

---

## Notes about the Maven output you saw
- **`No sources to compile`**: the archetype skeleton at that moment had no Java sources under `src/main/java`.
- **`skip non existing resourceDirectory ... src/test/resources`**: the folder doesn’t exist yet.
- **`BUILD SUCCESS`**: Maven completed WAR packaging successfully.

---

## Where did we “clone Spring framework”?
We did **not** clone the Spring Framework repository.

In your terminal steps, we only created a blank classic Spring MVC WAR project skeleton using a Maven archetype:

- `mvn archetype:generate -DarchetypeArtifactId=maven-archetype-webapp ...`
  - This creates the project structure (`src/main/webapp`, `pom.xml`, etc.) so you can later add Spring MVC dependencies.

## Do you need to clone Spring framework to use it?
No.

You typically do not clone Spring Framework. Instead, your project declares Spring dependencies in `core-spring-mvc/pom.xml`, and Maven downloads them from Maven Central (that’s the “dependencies” step—download jars).

## What’s next (to make it a real Core Spring MVC app)?
After the skeleton exists, we will:

1) Update `core-spring-mvc/pom.xml` to add:
- `spring-context`
- `spring-webmvc`
- servlet/JSP APIs as needed

2) Add classic configuration:
- `WEB-INF/web.xml` with `DispatcherServlet`
- a `@Configuration` class for component scanning + `ViewResolver`

3) Add a controller + view:
- `@Controller`
- JSP under `src/main/webapp/WEB-INF/views/`

So: the “clone” of Spring is done automatically by **Maven dependencies**, not by cloning Spring’s Git repo.

