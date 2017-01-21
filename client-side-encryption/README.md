# Client Side Encryption
Following functionality is to be provided by this modue.

* Client can encrypt a secret credential
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

* Client can sign and nonce a token
  * This is done to prevent replay of access token used by a client to access the resource server.
  * For the purpose of performance, signature can be done 
  The client uses the known public key of the server to send a s