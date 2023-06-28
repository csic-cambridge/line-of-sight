# Line of Sight

Line of Sight (LoS) is a methodology that addresses the development of asset information in relation to organisational objectives, especially addressing the disconnect between Asset Information Requirement (AIR) and
Organisational Information Requirement (OIR).

## Project Dependencies

The application requires the following technologies to be available in the system for a successful build and run of the application,

1. Java JDK version 17
2. Maven latest stable version
3. Angular
4. Liquibase
5. Docker (if application and/or database to be Dockerised)
6. MariaDB (as optional Docker Image)

## Getting Started
The application is available in two different versions: a full-featured, multi-user version ('full' version) and a lightweight, single-user version('showcase' version).

The full version requires a centralised backend consisting of a relational database and a java runtime environment running a jar file linked to a browser based front-end.  All users share the same project space for each installation.
Most of this readme covers the deployment of this version.

The showcase version requires a simple static website to serve the application to any number of single user browser frontends.  There is no linkage between any of the users or their data.  All users operate in a standalone fashion.

##Full Version
### Configuration File
Deployments of the full version of the application will require an external configuration file to be specified.
This will be a YAML file and will include
details of Authentication providers, database connections and other settings.  See the included application.yaml file for
an example of the format and settings which can be specified.  Other settings can be added if specific functionality or features are required.

### Authentication Providers
The application uses OIDC (Open ID Connect) authentication providers.  Sample Google and Azure configurations are in the example
but others may be added e.g. Facebook, Github etc. - see their documentation for the specific settings required.  
Accounts with these providers will need to be available.

### Database Connections
The application can function with most SQL databases but only MariaDb and MySQL have been tested.
The database connection string and credentials must be specified - see example file for MariaDb.
### Setting up Database
The Line of Sight Software application requires an external SQL database. 
For this document, **MariaDB** has been used to illustrate the setup.  It can be hosted
in a **Docker** container or standalone. The setup is explained below.  

* ### Docker Setup
    Docker can be used to host the back-end system and/or the database. 
**NOTE:** If Docker is not required, remove the file core/Dockerfile before doing a build.

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

  * *Windows* - https://docs.docker.com/desktop/install/windows-install/
  * *MacOS* - https://docs.docker.com/desktop/mac/install/
  * *Linux*
  
    Docker Desktop (beta) version is available for Linux (Ubuntu and Debian Distributions only). Information regarding this can be obtained from
[Docker Desktop For Linux(beta)](https://docs.docker.com/desktop/linux/)

    Instructions to install a more stable version of Docker on a Linux environment can be obtained from https://docs.docker.com/engine/install/

* ### MariaDB Database Set up
    MariaDB image may be hosted within a Docker container or as a standalone system. 
Follow the steps below with help in setting up a database.

 
  * If using Docker
  
    Install & run docker image
    ``` bash
    docker run --name mariadb -e MYSQL_ROOT_PASSWORD=mypass -p 3306:3306 -d docker.io/library/mariadb:10.3
    ```

    Start an interactive Bash session in the container
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
    CREATE USER cdbb@'%' IDENTIFIED BY 'cdbbpw';  (some systems may require a stronger password than this)
    GRANT ALL PRIVILEGES ON *.* TO 'cdbb'@'%';
    FLUSH PRIVILEGES;
    CREATE SCHEMA cdbb;
    ```

Now the database is set up and ready to be used by the application. 

### Line of Sight Software Application Set up

The source of the project is hosted at [csic-cambridge GitHub](https://github.com/csic-cambridge/line-of-sight/tree/main/line-of-sight-software-app-mk2). The latest version can be **cloned** from the repository using the command,

``` bash
git clone https://github.com/csic-cambridge/line-of-sight.git
```

* ### Running from source
    Open Terminal (MacOS / Linux) or Command Prompt (Windows). Navigate to the folder where project is cloned 

```
 cd line-of-sight\line-of-sight-software-app-mk2
   ```


and run the following commands,

```
    # Build the application 
     .\mvnw clean install -Ddev-build
  ```

  The created jar file will be:
   ```
    line-of-sight\line-of-sight-software-app-mk2\core\target\cdbb-core-<version>-dev.jar
  ```
  
* ### Run the application after successful build process
    ```
    .\mvnw -pl core --spring.config.location=file:<config file and path e.g. ../application.yaml> -Pdev spring-boot:run
    ```

    Once application has successfully started, open the following URL in a browser,
    
    ``` bash
    http://localhost:8080
    ```
    
  * Users on other computers must use the url of the hosting computer with port 8080.
  
  * Users will be presented with the configured name(s) of the authentication providers with which to login.

* ### Running from executable JAR

    If an executable JAR file is to be used, open Terminal (MacOS / Linux) or Command Prompt (Windows), navigate to the location of the jar file and run the following command,
    
    ``` bash
    java -jar cdbb-core-<version>-dev.jar --spring.config.location=file:<config file and path>
    ```
    
    This will run the application which can be accessed in a browser by navigating to **http://localhost:8080/**
  (or url of remote computer).

    If the JAR file isn't available, then see **Running from source** above.


* ### Running application as a Docker container
A Docker image can be created for the application with the following command

    ./mvnw docker:remove docker:build -P test-build-docker -P dev

To run the docker image make sure the database is set up and running beforehand.
To run a Docker database set up as above, run the following command:

```    
   docker run --network="host" --name mariadb -e MYSQL_ROOT_PASSWORD=mypass -p 3306:3306 -d docker.io/library/mariadb:10.3
 ```

then the application can be run as:

```
   docker run  --network="host" -v <absolute path to config.yaml file>:/application.yaml cdbb
 ```
 
There are many combinations of how the application and database can be run depending on which systems are run in Docker and how the network settings are required.
It is beyond the scope of this document to provide further options.

## Showcase Version
###Build
The build is done with the build of the main product.
It can be found in the frontend/dist/showcase folder.
All files in that folder should be statically served.

###Deployment
The showcase version requires a static website to be deployed.  Any number of independent users can run the application from the static website.
The static website files are generated during a build at frontend/dist/showcase.

There is no configuration required.  There is no login or concept of users.  All project data is stored within the browser but may be exported to a local file system.  
Exported files may be imported at any time, replacing the current project.


## Functionality
The functionality and working of the Line of Sight Software Application is detailed in the [User Manual](https://github.com/csic-cambridge/line-of-sight/wiki/User-Manual)

## License
[GPL v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html)
