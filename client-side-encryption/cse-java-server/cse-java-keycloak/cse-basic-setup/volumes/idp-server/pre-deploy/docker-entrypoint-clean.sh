#!/bin/bash

# Create a keycloak admin user. We will be user this user to configure keycloak inthe subsequents scripts.
/opt/jboss/keycloak/bin/add-user-keycloak.sh --user kcadmin --password kcadmin123
/opt/jboss/keycloak/bin/add-user.sh admin admin123 --silent

exec /opt/jboss/keycloak/bin/standalone.sh $@
exit $?