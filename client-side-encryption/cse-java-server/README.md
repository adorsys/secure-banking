# CSE Java Server

The intention of this module is to provide CSE implementations for the different java modules we use. For example
* Keycloak
* Spring Boot.

## Client can encrypt a secret credential

We have two use cases:

### 1. Client accessed the token endpoint of the ide with a credential

In this case client uses a secret credential to authenticate with the IDP. For example going to the token endpoint of the IDP with the password grant folw to acquire an access token.

We can protect this password by using the public key of the IDP to encrypt the password of the user.

### 2. Client want to send a form to the resource server with secret credential

A typical use case is the storage of the user PIN in a multibanking application.

In order to encrypt the secret credential send with the form (like a PIN), we need the public key of the resource server. This information can be stored in the oAuth token of the client by the IDP.

### TODOs:

* Define functionality to decrypt encrypted credential in the IDP using the IDP private key.
* Define functionality to decrypt a form parameter in the resource server using the resource server private key.
This should also include the checks of nonce and request timestamps and to store those from first and last requests in a sequence
    * Create a component to decrypt secret credentials with the server private key
        * JWTSecretCredentialDecryptor
            * decryptSecret(encryptedData Base64EncodedJWT, serverPrivateKey String) : String
    * Create a component to store and compare the nonce and timestamps of last request and to validate the expiration time
        * JWTRequestInPartialOrderValidator
            * validateRequest(JWT request): boolean
* Create a component to put resource server public key in the access token.
    * JWTPublicKeyIncluder
      * include(oAuthToken JWT, resourceServerPublicKey JWK): JWT
      * The name of the claim to include is "res_pub_key"
* Component to transform a PEM public key into a JWK
  * Some server will publish their pulic key just in a PEM encoded format.
  * JWKBuilder
    * build(publicKey:PemEcnodedPublicKey):JWK
