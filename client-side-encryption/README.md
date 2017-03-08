# Client Side Encryption
The client side encryption provide many feature to help protect sensitive information against infrastructure hazard. See https://en.wikipedia.org/wiki/TLS_termination_proxy .

With client side encryption, we want to address following issues:

* Encrypting sensitive information in the client before sending it to the server
* Sign and nonce oAuth Bearer token from being reused from inside the institution's own network

## Similar references

https://github.com/dxa4481/clientHashing

## Client can encrypt a secret credential
We use this approach to prevent disclosure of the sensitive information inside institutions internal networks. https://en.wikipedia.org/wiki/TLS_termination_proxy.

We see transport of a sensitive information and secret credentials in following situations:

* Client is authenticating with a site. The side can be either a standard server application or an identity provider. 
* Client is filling up a form with some sensitive information. This can be a registration form (in this case the password) or a form collecting credentials used for order services (an online banking PIN, a data encryption key).

In some cases, server will want to stored the credential in a way not recoverable in the server environment. For such a use case, we can also apply procedures called client hashing as shown in https://github.com/dxa4481/clientHashing.

In some other cases, credential will want to be recoverable by the server, because the server is:
*  Either no the final consumer of the credential (online banking PIN used by an AISP to access a legacy banking application)
* Or the client uses the credential to encrypt further data on the server and thus need to know the value of the credential. See https://github.com/adorsys/secure-key-storage/tree/master/private-key-recovery. This is generally called a credential encryption key. A credential encryption key is a key we use to encrypt client's critical information while storing them in recoverable manner in the institution's data center. Credential encryption keys allow the encryption of user information on an individual key basis. Means each user critical information is encrypted with a user specific secret key.

## Client can sign and nonce a token
A big problem a encounter in the world of oAuth2 is that the Bearer token is just a Bearer token. it is simply like cash. Who ever has access to the Bearer token can claim ownership of the Bearer. A Bearer token can be leaked in following situations:

* The internal network on the institution does not use SSL to exchange data among components. See https://en.wikipedia.org/wiki/TLS_termination_proxy
* Some component like reverse proxies write http request information into log files. Including the bearer token. With all the advanced logging capabilities provided by tools (like https://www.graylog.org/) it is obvious for some institution internal entities to have access to those token without any effort.

Additionally, we have no control on the expiration time of the access token. 

To respond to those thread, we can extends server functionalities not to access tokens (and cookies) without verifying the authenticity of the request sender. If we can allow client to provide the server with a HMAC-Key on the first request, subsequent use of the token can be nonced and signed by the HMAC-Secret to avoid those replay attacks.

Below is a sample workflow on how to nonce and sign a token:       

* For the purpose of performance, signature must be done with a symetric key (MAC) 
* The client uses the known public key of the resource server to register a MAC SecretKey with the resource server.
  * Each request sent by the client to the server is nonced (timestamped) and signed with the secret key
  * repeated request nonce is identified by the server as a replay.
If the server is deployed in a stateless clustered environment, it is sufficient to use the timestamp as a nonce and make sure expiration time is sufficiently short.

## Server Publi Key

In most of those cases, the client encrypting data for the server will have to be in possession the public key of the server. A client can access the public key of the server by the mean of:

### Configuration
* The client is statically compiled with the public key of the server. This approach bears the problem, that client code will break when the server renews his public key.
* Client is compiled with a static registry where server public key can be downloaded.
* The client is compile with the address of the server. The server provides an end point where client can download the server's public key. For example: https://keys.example.net/pop-keys.json

### Proof of Possession (RFC7800)
The public key of the server (in this case an oAuth2 relying party) can be embedded into the access token issued to the client. See https://github.com/adorsys/secure-key-storage/tree/master/client-side-encryption/cse-pop-spec
