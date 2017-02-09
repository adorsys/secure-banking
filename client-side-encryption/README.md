# Client Side Encryption
Following functionality is to be provided by this modue.

## Client can encrypt a secret credential
  * This is to prevent disclosure of the credential on the way between the client and the server.
  * We assume the transport of a secret credential in following situations
    * Client is authenticating with an idp (identity provider): 
      In this case we assume the client knows the public key of the identity provider. This can be given to the client :
      * by configuration. This is the case of keycloak where the client is built with a keycloak.json file or can download this file from the server.
      * by dowloading the idp server configuration from a known registery.
    * Client is filling up a form with secret credentials
      For example an online banking PIN to the server for processing. In this case the client has a security token provided by an idp.
      * Then client is sending the information to a resource server.
      * Resource server public key can be included into the token gotten from the idp.
    In both cases client will use the public key of the resource server to encrypt the secret credential.
	* Token used by the client to authenticate with a resource server contains secret credential encryption key
	  This occurs hen critical credentials are store in a recoverable maner on the server side, but encrypted on a user individual basis. Means each user secret credential is encrypted with another secret key.
	  * Then The identity provider will encrypt the secret credential key will be encrypted with the problick key of the server before being stored in the authentication token given to the client.
	  * This generally does not fall into the domain of client side encryption. Encryption and decryption of the credential encryption key occurs on the server side.
	    * Encryption is done by the idp
	    * decryption is done by the resource server       

## Client can sign and nonce a token
  * This is done to prevent replay of access token used by a client to access the resource server.
  * For the purpose of performance, signature must be done with a symetric key (MAC) 
  * The client uses the known public key of the resource server to register a MAC SecretKey with the with the resource server.
    * Each request sent by the client to the server is signed with the secret key and a nonce
    * repeated requests nonce is identified by the server as a replay.
    
## Message Format

Generally JOSE - JWS - JWE

### Client Encrypted Secret Credential

#### CLient authentication with an IdP

In this case the client knows the public key of the idp.

The is no specific additional protocol message needed. Use the public key of the idp to JWE encrypt the secret credential and put the base64 encoded JWE String in value of that form field.

#### CLient Filling up a for with secret credential

_Obsolete
Same as above. Use the public key of the resource server from the authentication token to JWE encrypt the secret credential an put the base64 encoded JWE String the value of the corresponding form field.

_New Information
Denys and i we just ell on this specification while trying to add a client public key key to the access token: https://tools.ietf.org/html/rfc7523

This specification describe how an idp can obtain the public key of the client to authenticat the client with a signed JWT. We can use the same approach to access the public key of a resource server and use it to encrypt informations.


For this approach the resource server must provide an endpoint that can be used by a client to retrieve the resource server public key.

#### Implementation Work

##### Forget this Section
* Component to extract access token
  * The oAuthToken sent from server might contain more than just an access token.
  * AccessTokenExtractor
    * extractAccessToken(oAuthToken:Base64EncodedJWT):Base64EncodedJWT
    * We assume oAuthToken contains the claim "access_token" or ist just an access token.
* Component to put and extract the json web key from the jwt token.
  * On the Server
    * JWTPublicKeyIncluder
      * include(oAuthToken JWT, resourceServerPublicKey JWK): JWT
      * The name of the claim to include is "res_pub_key"
  * JWTPublicKeyExtractor
    * extractKey(oAuthToken Base64EncodedJWT) : JWK // Token from server
	* We assume the JWT contains a claim named "res_pub_key"
	* The format and the location of this information might be dependent on the target identity provider.
	  * We prefer the IdP keeping this out of the access_token or refresh_token
	  * IdP can put this in the oAuthToken
	  * an identity provider might decide to put it accessToken because there is nothing else send to the client.
	  * So the public key extractor will be configured to work with the target id

##### Continue here: Encrypting secret infos.
* Component to JWE encrypt a string given a public key
  * SecretCredentialEncryptor
    * encrypt(secret:String, serverPublicKey:JWK) : Base64EncodedJWT
* Component to transform a PEM public key into a JWK
  * Some server will publish their pulic key just in a PEM encoded format.
  * JWKBuilder
    * build(publicKey:PemEcnodedPublicKey):JWK

### Client signed and nonced Token
Like described above, the purpose is to make sure a token sent by the client to the server can not be resent as result of a replay.
#### Workflow
In order to do this, the client must:
* For each new token
  * Generate a HMAC Key to be sent to the server
  * Encrypt the HMAC Key with the public key of the server
  * Generate a ClientJWT wrapping the Access Token with the following structure
  * Generate the first nonce and add to ClientJWT
  * Generate a timestamp and add to ClientJWT
  * Add very short expiration to ClientJWT
  * Include the encrypted HMAC Key in the first request to the server
  ```json
  {
  	"access_token":"access.token.from.idp",
  	"nonce":2341234,
  	"timestamp":"45654674657",
  	"hamac_key":"jwe.encrypted.hamac.key",
  	"expir":141234231
  }
  ```
  * HashMac sign and send ClientJWT to server : "Authorisation: Bearer hamac.sign.client.jwt"
* For each subsequent service request
  * Generate a ClientJWT wrapping the Access Token with the following structure
  * Generate the first nonce and add to ClientJWT
  * Generate a timestamp and add to ClientJWT
  * Add very short exiration to ClientJWT
  ```json
  {
  	"access_token":"access.token.from.idp",
  	"nonce":2341234,
  	"timestamp":"45654674657",
  	"expir":141234231
  }
  ```
  * HashMac sign and send ClientJWT to server : "Authorisation: Bearer hamac.sign.client.jwt"
#### Implementation Work
* Component to Generate HMACKey
  * HMACKeyGenerator
    * generateHMACKey(): JWK
* Component to Encrypt the HMACKey with public key of the server (We might add this to the secret credential encryptor class)
  * SecretCredentialEncryptor
    * encrypt(hmacKey:JWK, serverPublicKey:JWK) : Base64EncodedJWE
* Component to generate a nonce
  * NonceGenerator
    * generateNonce():Number
* Component to generate the timestamp
  * TimestamGenerator
    * generateTimeStamp():Number
* Component to build the ClientJWT object
  * ClientJWTBuilder
    * newClientJWT() : ClientJWT
    * withAccessToken(accessToken Base64EncodedJWT) : ClientJWT // 
    * withNonceGenerator(NonceGenerator) : ClientJWT
    * withTimestampGenerator(TimestamGenerator) : ClientJWT
    * withExpir(Number) : ClientJWT// The number of milliseconds of the validity
    * includeEncryptedHMAC(SecretCredentialEncryptor, serverPublicKey:JWK) : ClientJWT // used to encrypt the HMAC
    * build(hmacKey:JWK):Base64EncodedJWT
 


