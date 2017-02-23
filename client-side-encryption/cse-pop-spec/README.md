# Proof-of-Possession (rfc7800) 

We are using the rfc7800 to help a client application discover the public key of the server. Our main intention is to provide a client application with mean to send some secrets data to a resource server without having to rely on the ssl communication channel.

This module wants to isolate the functionality of resolving the public key of the resource server.

## Actors

### JWT Producer
In this module, we refer to a jwt producer as an application that can create and sign or encrypt a jwt. a JWT producer can be:
* The idp
  In this case the jwt can be an access token.
* A Relying Party
  In this case the JWT can be :
  * Part of an oAuth2 request with grant type "request"
* The Client
  * A JWE encrypted field sent in a form to the server

### JWT Consumer

The JWT consumer is the application that consumes the JWT. A consumer can be:
* The Idp
  Reading a jwt created by the relying party.
* The resource server (relying party)
  In this case reading the encrypted field sent by a client.
* Client
  Processing a an access token received from the idp.

## Use Cases

Followings use case will need a JWT consumer or producer to resolve the public key of the other party.

### Consumer need Public Key of Producer to verify Signature of JWT

The consumer of a JWT needs the public key of the producer of a JWT to verify the authenticity of the JWT. This is the case when:
* The IDP need the public key of the Relying Party to verify the authenticity of the oAuth Request with grant type "request"
* The relying party need a public key of the IDP to verify the authenticity of the oAuth token.

### Producer needs Public Key of Consumer to Encrypt Data sent to Consumer

This is mostly the case when the JWT produced is supposed to contain data not to be disclosed to the public. Following examples apply:
* Client application wants to encrypt a password before sending the login form to the idp
* Client application wants to encrypt a PIN (personal identification number) before sending the form to the resource server
* Client application wants to sign and nonce each JWT before using it as a bearer token in order to avoid reuse of token leaking.
* Client application wants to send a HMAC-Secret to server for use in subsequent sessions for signature.

## How do we retrieve a Public Key

### Public Key as Configuration Info

This mostly happens in oAuth2 processes. The relying party is configured with the public key of the idp server. This is the case when we configure a keycloak adapter. There we generally provide the client(relying party) with a keycloak.json that contains a PEM version of the public key of the keycloak idp.

### Public Key following rfc7800 (Proof of Possession)

If the client is in possession of an access token issued by the authorization server, the client can check if the jwt contains a confirmation claim. If this is the case, the client can retrieve the public key of the resource server from the confirmation claim. The confirmation claim can have the following forms:

```
{
  "iss": "https://server.example.com",
  "aud": "https://client.example.org",
  "exp": 1361398824,
  "cnf":{
    "jwk":{
      "kty": "EC",
      "use": "sig",
      "crv": "P-256",
      "x": "18wHLeIgW9wVN6VD1Txgpqy2LszYkMf6J8njVAibvhM",
      "y": "-V4dS4UaLMgP_4fY4j8ir7cl1TXlFdAgcx55o7TkcSA"
     }
   }
}
```
In this case the confirmation claim contains the public key of the resource server.

The confirmation can also be directly retrieved from the resource server. For this cases the idp will include the target url into the access token and the access token will look like:
```
{
  "iss": "https://server.example.com",
  "sub": "17760704",
  "aud": "https://client.example.org",
  "exp": 1440804813,
  "cnf":{
    "jku": "https://keys.example.net/pop-keys.json",
    "kid": "2015-08-28"
   }
}
```

The content of the response of https://keys.example.net/pop-keys.json muss be a set of jwk ("keys":[]) like defined by https://tools.ietf.org/html/rfc7517 . If the response returns more than on JWK, each jwk in the set will have a key id "kid" to allow for selection of the desired key. It the confirmation claim did not provide a key id, and the response of the "jku" request returns more than one JWK, the most appropriate key shall be use for encryption. In this case the one containing the claim "use":"enc".  

For further readings, see:
https://tools.ietf.org/html/rfc7800
https://www.youtube.com/watch?v=ZF0wrHtiXYw
http://www.iana.org/assignments/jwt/jwt.xhtml

### Public Key Available by convention

For simplification purpose, and in order t relieve the burden and dependency from idp servers, this framework will specify an endpoint that when available will provide the public key of the server processing the requests.

#### Base URL (${base_url}/pop-keys.json)

We assume the base URL is the url used by a server to service requests to clients. In general, the base url will look like: https://server.domain/context-root.

in case the access token does not provide any PoP information, the client application can try to access the public key of the resource server at: ${base_url}/pop-keys.json (for example: https://server.domain/context-root/pop-keys.json). The response to this request must be a JWK Set like defined in rfc7517.

If the response contains more than one key, the key used for encryption must be selected in the following order:

* If there is a JWK with the claim "use":"enc", this key must me user to encrypt data sent to the target server.
* Else any public key can be used to send encrypted data to the server.

## Identifying a Public Key in the Client Application

The client application will resolve an cache resource servers public keys. In order o identify a key, we user the String {base_url}/{kid}/{use}. We assume that is the resource server has more than one key with the same id, the use of the key will help distinguish them and select the correct key.


We use the following pattern to resolve a key in the client application.

Let the "use" be the reason for which we need the key. This can be either to verify a signature "use":"sig" or encrypt for the server "use":"enc".

* If we have an access token for the resource server
  * Check if the access token contains a confirmation claim.
    * Check if this claim is a public key "jwk". 
      * Then extract the public key and use it for the intended purpose.
      * Store the key in the bag for later use.
    * Check if the claim is the url of the public key "jku". 
      * Then extract the {base_url} and the {kid} from the confirmation claim. 
* If the cleint app does not have an access token or the token does not contain a confirmation claim
  * Read the base url of the resource server from the application configuration
  
From here we either have the key or the information on the origine of the key in the form of {base_url}/{kid}/{use}. Where kid is optional.
* First get the bag of key at {base_url}
  * If we have the key id 
    * First look for the key with pattern : {base_url}/{kid}/{use}
    * Then look for the key with pattern : {base_url}/{kid}
    * Then look for the key with any pattern : {base_url}/{kid}/*
  * If we have no kid
    * First look for the key with pattern : {base_url}/*/{use}
    * Then look for the key with pattern : {base_url}/*
    * Then look for the key with any pattern : {base_url}/*/*
* If no key found in the bag, 
  * then get the list of keys at {base_url}
  * Proceed to key selection like define above.
  * store selected keys in the bag for reuse.
