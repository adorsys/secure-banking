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

Same as above. User the public key of the resource server from the authentication token to JWE encrypt the secret credential an put the base64 encoded JWE String the value of the corresponding form field.

In this case we need to specify the name of the jwt claim carrying the public key of the resource-server:

Claim Name : **res_pub_key**
Claim Value : 
```json
	{
      "kty":"EC",
      "kid":"i0wng",
      "use":"sig",
      "x":"AXYMGFO6K_R2E3RH42_5YTeGYgYTagLM-v3iaiNlPKFFvTh17CKQL_OKH5pEkj5U8mbel-0R1YrNuraRXtBztcVO",
      "y":"AaYuq27czYSrbFQUMo3jVK2hrW8KZ75KyE8dyYS-HOB9vUC4nMvoPGbu2hE_yBTLZLpuUvTOSSv150FLaBPhPLA2",
      "crv":"P-521"
    }
```
Example JWT:

```json
{
  "sub": "1234567890",
  "name": "John Doe",
  "res_pub_key": {
      "kty":"EC",
      "kid":"i0wng",
      "use":"sig",
      "x":"AXYMGFO6K_R2E3RH42_5YTeGYgYTagLM-v3iaiNlPKFFvTh17CKQL_OKH5pEkj5U8mbel-0R1YrNuraRXtBztcVO",
      "y":"AaYuq27czYSrbFQUMo3jVK2hrW8KZ75KyE8dyYS-HOB9vUC4nMvoPGbu2hE_yBTLZLpuUvTOSSv150FLaBPhPLA2",
      "crv":"P-521"
    }
}

#### Needed Implementation Work

* Component to JWE encrypt a string given a public key
	* SecretCredentialEncryptor
	  * encrypt(String secret, JWK key) : JWE
* Component to extract the json web key from the jwt token.
	* JWTPublicKeyExtractor
	  * extractKey(JWT jwt) : JWK
	* We assume the JWT contains a claim named "res_pub_key"

### Client signed and nonceed a token
Like describe above, the purpose ist to make sure a token sent by the client to the server can not be resent as result of a replay.

 