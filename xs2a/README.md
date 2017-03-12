# PSD2 Access 2 Account
This is a transitional API mapping the PSD2 requirement that each account servicing payment service provider (ASPSP) must provide an interface for account information service providers (AISP) to read payment service users (PSU) account information and account statements.

The service uses hbci in the background to access PSU banking information.

To build the project:

### hbci4java fork 

 ```
 git clone https://github.com/tadschik/hbci4java.git
 mvn clean install -f hbci4java/pom.xml
 ```

## Build the multibanking adapter

 ```
 git clone https://github.com/adorsys/multibanking.git
 mvn clean install -f multibanking/onlinebanking-adapter/pom.xml
 ```

## Build this project and start with wildfly swarm

 ```
 mvn clean install wildfly-swarm:run
 ```
