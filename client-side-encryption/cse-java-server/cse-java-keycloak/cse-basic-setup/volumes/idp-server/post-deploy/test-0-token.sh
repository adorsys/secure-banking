#!/bin/bash

if [ -z $1 ] 
	then
		echo "Missing host and port"
		echo "Usage: test-0-token.sh <host:port> <client-id>"
		exit 1
fi

hostAndPort=$1


TKN='test'
while [ "$TKN" == 'test' ]; do
	echo "Checking idp server"
	# Get and parse access token
	RESP=$(curl -s -X POST "http://$hostAndPort/auth/realms/master/protocol/openid-connect/token" -D tmp.txt -H "Content-Type: application/x-www-form-urlencoded" -d "username=denys" -d 'password=denys' -d 'grant_type=password' -d 'client_id=cse-client')
	if [[ "$RESP" == *"access_token"* ]]
	then
	  TKN=`echo $RESP | sed 's/.*access_token":"//g' | sed 's/".*//g'`
	  echo "Idp is ready"
	else
	  cat tmp.txt
	  echo "Still waiting for idp to be ready"
	  sleep 2
	fi
done

echo $TKN