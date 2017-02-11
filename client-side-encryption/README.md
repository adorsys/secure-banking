# Client Side Encryption
Following functionality is to be provided by this modue.

## Client can encrypt a secret credential
  * This is to prevent disclosure of the credential on the way between the client and the server.
  * We assume the transport of a secret credential in following situations
    * Client is authenticating with an idp (identity provider): 
      In this case we assume the client knows the public key of the identity provider. This can be given to the client :
      * by configuration. This is the case of keycloak where the client is built with a keycloak.json file or can download this file from the server.
      * by dowloading the idp server configuration from a known registry.
      
    * Client is filling up a form with secret credentials or sending some secret credentials to the resource server (password, PIN, credential encryption key)
      For example an online banking PIN to the server for processing. In this case the client has a security token provided by an idp. A credential encryption key is a seed we use to encrypt client's critical information while storing them i a recoverable manner on the server. These credential encryption keys allow the encryption of user information on an individual key basis. Means each user critical information is encrypted with a user specific secret key.
      Then client is sending the information to a resource server.
      Resource server public key must be available to the client application. Client can obtain public key from a public key endpoint provided by the resource server. The endpoint can look like: https://mydomain/context-root/k_jwks. A call to this endpoint will return the resource server's public key in jwk or pem format.
      
    In both cases client will use the public key of the resource server/idp to encrypt the secret information.
    * Idp Managed Credential Encryption Keys
    A credential encryption key is an secret key that can be use in a computing environment to protect critical user information. The credential encryption key is generally crentrally managed by the idp. Critical information that are encrypted by the credential encryption key are thus stored in the resource environment.
    When the idp server is producing a token for a resource server, the idp server can use the public key of the resource server to encrypt the credential encryption key of the user on that resource server.
	This generally does not fall into the domain of client side encryption. Encryption and decryption of the credential encryption key occurs on the server side. Encryption is done by the idp. Decryption is done by the resource server       

## Client can sign and nonce a token
  * This is done to prevent replay of access token used by a client to access the resource server.
  * For the purpose of performance, signature must be done with a symetric key (MAC) 
  * The client uses the known public key of the resource server to register a MAC SecretKey with the with the resource server.
    * Each request sent by the client to the server is signed with the secret key and a nonce
    * repeated requests nonce is identified by the server as a replay.
    
### Message Format

Generally JOSE - JWS - JWE

### Client Encrypted Secret Credential

#### CLient authentication with an IdP

In this case the client knows the public key of the idp.

The is no specific additional protocol message needed. Use the public key of the idp to JWE encrypt the secret credential and put the base64 encoded JWE String in value of that form field.

#### CLient Filling up a form with secret credential

For this approach the resource server must provide an endpoint that can be used by a client to retrieve the resource server public key. The public key of a resource server can be obtained at that resource server endpoint site like: https://mydomain/context-root/k_jwks

#### Implementation Work
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
  * Generate the first nonce and add to ClientJWT (use jwi claim to store nonce)
  * Generate a timestamp and add to ClientJWT (use the iat claim to store timestamp)
  * Add very short expiration to ClientJWT (use exp claim to store the expiration time)
  * Include the encrypted HMAC Key in the first request to the server
  ```json
  {
  	"access_token":"access.token.from.idp",
  	"jwi":2341234,
  	"iat":45654674657,
  	"hamac_key":"jwe.encrypted.hamac.key",
  	"exp":141234231
  }
  ```
  * HashMac sign and send ClientJWT to server : "Authorisation: Bearer hamac.sign.client.jwt"
  
* For each subsequent service request
  * Generate a ClientJWT wrapping the Access Token with the following structure
  * Generate the a nonce and add to ClientJWT (use jwi claim to store nonce)
  * Generate a timestamp and add to ClientJWT (use the iat claim to store timestamp)
  * Add very short exiration to ClientJWT (use exp claim to store the expiration time)
  ```json
  {
  	"access_token":"access.token.from.idp",
  	"jwi":2341234,
  	"iat":45654674657,
  	"exp":45654674957
  }
  ```
  * HashMac sign and send ClientJWT to server : "Authorisation: Bearer hamac.sign.client.jwt"
  
#### Formal specification of Implementation Work
* Component to Generate HMACKey
  * HMACKeyGenerator
    * generateHMACKey(): JWK
* Component to Encrypt the HMACKey with public key of the server (We might add this to the secret credential encryptor class)
  * SecretCredentialEncryptor
    * encrypt(hmacKey:JWK, serverPublicKey:JWK) : Base64EncodedJWE
    * This is only used to encrypt the HMAC-Key in the first request. Do not encrypt the jwt.
* Component to generate a nonce
  * NonceGenerator
    * generateNonce():Number
    * This shall be availbe everywhere.
    
* Component to generate the timestamp
  * TimestamGenerator
    * generateTimeStamp():Number
    * This shall be availbe everywhere.
    
* Component to build the ClientJWT object
  * ClientJWTBuilder
    * newClientJWT() : ClientJWT
    * withAccessToken(accessToken Base64EncodedJWT) : ClientJWT // 
    * withJWI(NonceGenerator) : ClientJWT
    * withIAT(TimestamGenerator) : ClientJWT
    * withEXP(Number) : ClientJWT// The number of milliseconds of the validity
    * includeEncryptedHMAC(SecretCredentialEncryptor, serverPublicKey:JWK, hmacKey:JWK) : ClientJWT // used to encrypt the HMAC
    * build(hmacKey:JWK):Base64EncodedJWT
 


