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
At least one authentication provider account is required to be available.

####Configuration Notes
The redirect url for an authentication provider is defined by the application through Spring Boot and will be:

{baseUrl}/login/oauth2/code/{registrationId}

where registrationId is the name of the provider in the configuration file (application.yaml) under

security:
oauth2:
client:
registration:

It is important that the authentication provider sends the email address of the user in the 
redirect message so that the application can create the user's account.  
Some providers do this automatically e.g. Google, others require it to be set up explicitly
in the authentication account e.g. Azure.
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

AWS Deployment Guide
=========================

This guide provides a detailed walkthrough of how to deploy the CDBB application using AWS Elastic Beanstalk, AWS RDS MariaDB, and configuring it with the necessary environment variables.

Prerequisites:
--------------

-   AWS account with required privileges
-   Knowledge of AWS services mentioned in the guide
-   The CDBB application source code

Deployment Steps:
-----------------

### 1\. AWS EC2 Configuration

-   Create EC2 Instance Profile:

    -   An instance profile is a container for an AWS Identity and Access Management (IAM) role that you can use to pass role information to an EC2 instance at launch time.
    -   Navigate to the AWS Management Console and then to the IAM dashboard.
    -   Choose "Roles" and then "Create Role".
    -   In the AWS service role type, choose "EC2" and then select "EC2" again for the use case. Follow the prompts to create the role with necessary permissions.
-   Create EC2 Key pair:

    -   Key pairs are used to securely log in to your EC2 instances.
    -   In the EC2 dashboard, under "Network & Security", click "Key Pairs".
    -   Click "Create Key Pair" and provide a name.
    -   Download and securely store the private key.

### 2\. AWS Elastic Beanstalk Environment

-   Create AWS Elastic Beanstalk environment:
    -   Navigate to the Elastic Beanstalk console.
    -   Click "Create New Environment".
    -   Follow the wizard and select the desired platform, in this case, Java.
    -   You'll configure the environment details later after the database setup.

### 3\. Database Setup with AWS RDS MariaDB

-   Create AWS RDS database with MariaDB:

    -   Navigate to the RDS dashboard.
    -   Click "Create Database".
    -   Choose "MariaDB" as the database type.
    -   In the connectivity section:
        -   Select connect to the EC2 instance and choose the Beanstalk EC2 instance you've created.
        -   For the DB subnet group, select "auto-setup".
        -   Create a new VPC security group.
    -   In the "Additional configuration" section, name your initial database as "cdbb".
-   Database Accessibility:

    -   To connect to the database outside AWS, make sure RDS is publicly accessible via its configurations.
    -   Update the security groups to allow inbound connections on port `3306` and specify the desired IP addresses that can access the RDS.
    -   For a secure approach, set up an EC2 instance within AWS as a 'jump' box to perform database admin tasks. This acts as a secure bridge for database access.

### 4\. Building & Deploying the Application

-   Configure application.yml:

    -   Locate the `application.yml` file under `core/src/main/resources/`.
    -   Update the `spring.datasource` property with your RDS connection details.
-   Build the JAR with the Beanstalk environment configuration:

    -   Depending on your build tool (like Maven or Gradle), execute the relevant command to produce the JAR file of your application.
-   Upload & Deploy JAR to the Beanstalk environment:

    -   Navigate back to your Elastic Beanstalk environment in the AWS console.
    -   Upload the JAR file and deploy it.

### 5\. Set Environment Variables

-   In the Elastic Beanstalk environment dashboard:
    -   Set `SERVER_PORT` to `5000`.
    -   Configure `web.cors.allowed-origins` with your Beanstalk deployment domain.
    -   Configure `security.oauth2.client.registration` with details of your OAuth2 providers (e.g., Google/AzureAD).

### 6\. OAuth2 Configuration

-   Update OAuth2 providers with deployed Beanstalk URLs:
    -   Ensure that the URLs (redirect URLs and others) of your OAuth2 providers, like Google or AzureAD, are updated to point to the correct endpoints on your Beanstalk environment.

### 7\. Configuring SSL for Your Beanstalk Environment

Ensuring encrypted communication to your deployed application is paramount for security. Here's a step-by-step guide on how to configure SSL for your application deployed on AWS Elastic Beanstalk.

### Acquiring an SSL Certificate

1.  Navigate to Amazon Certificate Manager (ACM):

    -   Access the AWS Management Console.
    -   Open the Amazon Certificate Manager.
2.  Request a Certificate:

    -   Choose Request a certificate.
    -   Select Request a public certificate and provide your domain name details. Follow the steps to validate your domain either through DNS or email validation.
    -   Upon validation, the certificate status will change to `Issued`.

### Configuring Elastic Load Balancer (ELB) with SSL

Elastic Beanstalk applications typically use an Elastic Load Balancer to manage incoming traffic. To ensure secure communication, you can attach the SSL certificate to this load balancer.

1.  Access Elastic Beanstalk Dashboard:

    -   Select your application environment.
2.  Navigate to Configuration:

    -   In the navigation pane, choose Configuration.
3.  Edit Load Balancer Settings:

    -   Locate the Load Balancer category and select Edit.
    -   In the Load balancer settings section:
        -   For Secure listener port, input `443`.
        -   Set the Protocol to HTTPS.
        -   Under SSL certificate ID, choose the SSL certificate you provisioned with ACM.
4.  Apply Changes:

    -   Click on Apply to save your settings.

### Redirect HTTP to HTTPS

To ensure that all traffic is encrypted, you can set up a redirect to forward all HTTP traffic to HTTPS without modifying the application code:

1.  Access the Elastic Load Balancing Dashboard in EC2 Console:

    -   On the navigation pane, under Load Balancing, choose Load Balancers.
2.  Choose Your Load Balancer:

    -   Identify the load balancer associated with your Beanstalk environment. It's typically prefixed with "awseb".
3.  Modify Listener Rules:

    -   Navigate to the Listeners tab.
    -   For the listener on HTTP : 80, select View/edit rules.
    -   Click the pencil icon to edit.
    -   Add a condition for source and target:
        -   For source, select `HTTP` and `source port 80`.
        -   For the target, opt for `HTTPS` and `target port 443`.
    -   Confirm by clicking the checkmark icon.
4.  Save the Rules:

    -   Confirm your changes by selecting the checkmark icon once again.
5.  Validation:

    -   As a final step, test your application by trying to access it over HTTP. It should automatically redirect you to HTTPS, ensuring your traffic remains encrypted.

Following these steps, your application on AWS Elastic Beanstalk will be securely configured with SSL.

Additional Notes:
-----------------

1.  The [AWS documentation](https://aws.amazon.com/blogs/devops/deploying-a-spring-boot-application-on-aws-using-aws-elastic-beanstalk/) can be a valuable resource to assist with many of the aforementioned steps.

2.  Remember, making the RDS database publicly accessible might expose it to risks. Always ensure that only the required IPs are whitelisted and consider using secure methods like a jump box for administration.

With these steps completed, your CDBB application should be up and running on AWS Elastic Beanstalk with a connected MariaDB instance on RDS. Ensure to perform regular checks and monitoring to ensure the smooth operation of your deployment.

## License
[GPL v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html)
