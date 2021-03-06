#!/bin/bash

if [ -z $1 ] 
	then
		echo "Missing host and port"
		echo "Usage:`basename $0` <host:port> <client-id>"
		exit 1
fi

hostAndPort=$1

TKN='test'
while [ "$TKN" == 'test' ]; do
	echo "Checking idp server"
	# Get and parse access token
	RESP=$(curl -s -X POST "http://$hostAndPort/auth/realms/master/protocol/openid-connect/token" -D tmp.txt -H "Content-Type: application/x-www-form-urlencoded" -d "username=kcadmin" -d 'password=kcadmin123' -d 'grant_type=password' -d 'client_id=admin-cli')
	if [[ "$RESP" == *"access_token"* ]]
	then
	  TKN=`echo $RESP | sed 's/.*access_token":"//g' | sed 's/".*//g'`
	  echo "Idp is ready"
	else
	  echo "Still waiting for idp to be ready"
	  sleep 2
	fi
done

# read public key and stroe in file realm-master-keys.json
jsonFile=`echo "./realm-master-keys.json"`
curl -s "http://$hostAndPort/auth/admin/realms/master/keys" -H "Authorization: Bearer $TKN" -H 'Accept: application/json, text/plain, */*' -D response-header.txt > $jsonFile

# cleanup
# rm response-header.txt 

