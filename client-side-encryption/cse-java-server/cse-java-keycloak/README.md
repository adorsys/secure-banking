# CSE Java Server

The intention of this module is to provide CSE implementations for the different java modules we use. For example
* Keycloak
* Spring Boot.

## Client can encrypt a secret credential

We have two use cases:

### Client accessed the token endpoint of the ide with a credential

In this case client uses a secret credential to authenticate with the IDP. For example going to the token endpoint of the IDP with the password grant folw to acquire an access token.

We can protect this password by using the public key of the IDP to encrypt the password of the user.

### Client want to send a form to the resource server with secret credential

A typical use case is the storage of the user PIN in a multibanking application.

In order to encrypt the secret credential send with the form (like a PIN), we need the public key of the resource server. This information can be stored in the access token of the client by the IDP.

### TODO

* Define functionality to decrypt encrypted credential in the IDP using the IDP private key.
* Define functionality to decrypt a form parameter in the resource server using the resource server private key.
* Define an IDP extension to put resource server public key in the access token.

Both functionalities are the same. I Means use a private key to decrypt the client encrypted credential.