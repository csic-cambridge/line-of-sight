# Line of Sight

Line of Sight (LoS) is a methodology that addresses the development of asset information in relation to organisational objectives, especially addressing the disconnect between Asset Information Requirement (AIR) and
Objective Information Requirement (OIR).

## Project Dependencies

The application require the following technologies to be available in the system for a successful build and run of the application,

1. Java JDK version 17
2. Maven latest stable version
3. Angular
4. Liquibase
5. Docker
6. MariaDB Docker Image

## Getting Started

### Setting up Database
The final deployment of Line of Sight Software application enables Data Persistence. For this purpose, instead of **in-memory** 
database, an external database has been set up. For this purpose, **MariaDB** has been used which will be hosted
in a **Docker** container. The set up of these have been explained further.  

* ### Docker Setup
    For Data persistence, docker is used to host a MariaDB image. Docker setup is different for Windows/MacOs and Linux systems and
is explained in the sections below.

  * #### Windows / MacOS
    Docker Desktop is used to install Docker on the Windows and MacOS systems. Docker Desktop is an easy to install application to
build and share containerized applications and microservices. Docker Desktop includes the following,
    * Docker Engine
    * Docker CLI client
    * Docker Compose
    * Docker Content Trust
    * Kubernetes
    * Credential Helper

    In order to install Docker Desktop, follow the instructions at the following links,

    *Windows* - https://github.com/csic-cambridge/line-of-sight/tree/main/line-of-sight-software-app-mk2
*MacOS* - https://docs.docker.com/desktop/mac/install/

  * #### Linux
    Docker Desktop (beta) version is available for Linux (Ubuntu and Debian Distributions only). Information regarding this can be obtained from
[Docker Desktop For Linux(beta)](https://docs.docker.com/desktop/linux/)

    Instructions to install a more stable version of Docker on a Linux environment can be obtained from https://docs.docker.com/engine/install/

* ### MariaDB Database Set up
    MariaDB image is hosted within a Docker Container. Following the steps below with help in setting up a database, 

  * Install & run docker image
    ``` bash
    docker run --name mariadb -e MYSQL_ROOT_PASSWORD=mypass -p 3306:3306 -d docker.io/library/mariadb:10.3
    ```

  * Start an interactive Bash session in the container
    (This is not required on Windows as the UI provides a CLI)
    ``` bash
    docker exec -ti mariadb bash
    ```

  * Start mysql
    ``` bash
    mysql -u root -p
    (use mypass as password when prompted)
    ```

  * Create the user for the system
    ``` bash
    CREATE USER cdbb@'%' IDENTIFIED BY 'cdbbpw';
    GRANT ALL PRIVILEGES ON *.* TO 'cdbb'@'%' IDENTIFIED BY 'cdbbpw';
    FLUSH PRIVILEGES;
    CREATE SCHEMA cdbb;
    ```

Now Database is set up and ready to be used by the application. 

### Line of Sight Software Application Set up

The source of the project is hosted at [csic-cambridge GitHub](https://github.com/csic-cambridge/line-of-sight/tree/main/line-of-sight-software-app-mk2). The latest version can be **cloned** from the repository using the command,

``` bash
git clone https://github.com/csic-cambridge/line-of-sight.git
```

* ### Running from Source
    Open Terminal (MacOS / Linux) or Command Prompt (Windows). Navigate to the folder where project is cloned and run the following commands,

    ```bash
    # Build the application 
     .\mvnw clean install -Ddev-build
    
    # Run the application after successful build process
    .\mvnw -pl core -Pdev spring-boot:run
    ```

    Once application has successfully started, open the following URL in a browser,
    
    ``` bash
    http://localhost:8080
    ```
    
    User will be presented with the login screen.

* ### Running from executable JAR

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

## Functionality
The functionality and working of the Line of Sight Software Application is detailed in the [User Manual](https://github.com/csic-cambridge/line-of-sight/wiki/User-Manual)

## License
[GPL v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html)
