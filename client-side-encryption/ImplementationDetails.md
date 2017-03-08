# Sample Design

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
 