# Line of Sight

Line of Sight (LoS) is a methodology that addresses the development of asset information in relation to organisational objectives, especially addressing the disconnect between Asset Information Requirement (AIR) and
Objective Information Requirement (OIR).

## Getting Started

## Project Dependencies

The application require the following technologies to be available in the system for a successful build and run of the application,

1. Java JDK version 17
2. Maven latest stable version
3. Angular
4. Liquibase
5. Docker


## Installation

The source of the project is hosted at [csic-cambridge GitHub](https://github.com/csic-cambridge/line-of-sight/tree/main/line-of-sight-software-app-mk2). The latest version can be **cloned** from the repository using the command,

``` bash
git clone https://github.com/csic-cambridge/line-of-sight.git
```

### Running from Source
Open Terminal (MacOS / Linux) or Command Prompt (Windows). Navigate to the folder where project is cloned and run the following commands,

```bash
# Build the application 
 .\mvnw clean install -Ddev-build

# Run the application after successful build process
.\mvnw -P dev core spring-boot:run
```

Once application has successfully started, open the following URL in a browser,

``` bash
http://localhost:8080
```

User will be presented with the login screen.

### Running from executable JAR

Considering an executable JAR file is available to the user, open Terminal (MacOS / Linux) or Command Prompt (Windows), navigate to the location of the jar file and run the following command,

``` bash
java -jar cdbb-core-<version>-dev.jar
```

This will run the application and then application can be accessed in a browser by navigating to **http://localhost:8080/**

If the JAR file isn't available, then following the steps of **Running from Source**, use the **clean install** command.

```bash
# Build the application 
 .\mvnw clean install -Ddev-build
```

Once completed, navigate to the **target** directory to retrieve the JAR file. The location of target directory is,

```bash
# Location
\line-of-sight\line-of-sight-software-app-mk2\core\target

# File
cdbb-core-<version>-dev.jar
```

## License
[GPL v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html)