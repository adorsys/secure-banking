#!/bin/sh

# this script is to be run in a docker container

# change to project directory
cd /usr/src/app

# Build project
mvn clean install

# Build hbci4java fork
git clone https://github.com/tadschik/hbci4java.git
mvn clean install -f hbci4java/pom.xml

# Build the multibanking adapter
git clone https://github.com/adorsys/multibanking.git
mvn clean install -f multibanking/onlinebanking-adapter/pom.xml

# change to project directory
cd /usr/src/app/xs2a
# run xs2a project
mvn clean install wildfly-swarm:run
