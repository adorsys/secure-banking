# PSD2 Access 2 Account
This is a transitional API mapping the PSD2 requirement that each account servicing payment service provider (ASPSP) must provide an interface for account information service providers (AISP) to read payment service users (PSU) account information and account statements.

The service uses hbci in the background to access PSU banking information.

## Building and running

The easy way using docker is described [here](../README.md#xs2a)
If you want to do it on your own you have to build the parent directory, the hbci4java fork and the multibanking adpater as described below.

### hbci4java fork

 ```bash
 git clone https://github.com/tadschik/hbci4java.git
 mvn clean install -f hbci4java/pom.xml
 ```

### Build the multibanking adapter

 ```bash
 git clone https://github.com/adorsys/multibanking.git
 mvn clean install -f multibanking/onlinebanking-adapter/pom.xml
 ```

### Build this project and start with wildfly swarm

 ```bash
 mvn clean install wildfly-swarm:run
 ```

## Testing

### Swagger UI

Follow Link to the documented [Swagger API](
    http://localhost:8080/swagger-ui/?url=http://localhost:8080/swagger.json)
