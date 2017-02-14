# PKR Java Server

This module defines the private key recovery from the perspective of a java resource server.

## TODO
### Implement Key Generator

The key generator is a component that resides in the key using application. A key generator generates the key pair needed by an application and encrypts it with a user provided password. This always exists as a standard feature in most crypto tools. The key generator will have the following abstract interface:
* generateKey(KeySpec): BinaryKeyPair or KeyPair.JWK
* protectKeyPair(masterPassword): EncryptedKeypair_Main.JWE
  * storeOnLocalDevice(EncryptedKeypair_Main.JWE)
  For the case of a java server, we have folowwing options:
  
  * The key generating component is the idp server. 
    * In this case, we use he user provided password and the user id as both initial seed to generate the "masterPassword"
      * Provide the possibility to read the resource server public key in the idp while creating an access token for the resource server.
        readResourceServerPublicKey()
      * Generate a generate a credential encryption key for this resource server
        generateCredentialEncryptionKey(KeySpec): SecretKey.JWK
        protectCredentialEncryptionKey(SecretKey.JWK, masterPrivateKey): EncryptedSecretKey_ResourceServer.JWE
        storeEncryptedSecretKeyInUserAttributes(EncryptedSecretKey_ResourceServer.JWE)
      * include the credential encryption key into the user access token
        publicKeyEncryptCredentialEncryptionKey(SecretKey.JWK, ResourceServerPublicKey.JWK):PublicKeyEncryptedCredentialEncryptionKey.JWE
        includeCredentialEncryptionKeyIntoAccessToken(PublicKeyEncryptedCredentialEncryptionKey.JWE)
    * The password change processes have to be considered. So while changing the user password, we will also have to change the user generated masterPassword. 
      * extend idp password change process to rekey master key record when user changes password given an older password.
        rekeyMasterKey(oldMasterPassword, newMasterPassword)
      * Open issue: password forgotten.
        The password forgotten process must be designed such as to provide a way to make the recovery device a trusted party. If not we wont have any way to set a new master password when then user doe not provide the old one. 
  
  * The key generating component is a resource server.
    * We require the idp to generate and include a credential encryption key in the user access token.
    * The included credential encryption key will be protected with the public key of the resource server.
    * Refresh token process
      When the user logs in with his password, the idp produces the oAuthToken(acessToken, refreshToken) and forgets the user password. In order for the idp server produce subsequent access token from the refresh token, the oauth server will have to put a master key encryption key in the refresh token.
      * Generate a generate a credential encryption key for the idp server (same as above for resource server)
        generateCredentialEncryptionKey(KeySpec): SecretKey.JWK
        protectCredentialEncryptionKey(SecretKey.JWK, masterPrivateKey): EncryptedSecretKey_IdPServer.JWE
        storeEncryptedSecretKeyInUserAttributes(EncryptedSecretKey_IdPServer.JWE)
      * include the credential encryption key into the user refresh token
        publicKeyEncryptCredentialEncryptionKey(SecretKey.JWK, IdPServerPublicKey.JWK):PublicKeyEncryptedCredentialEncryptionKey.JWE
        includeCredentialEncryptionKeyIntoRefreshToken(PublicKeyEncryptedCredentialEncryptionKey.JWE)
