# PKR Keycloak Extension

Develop a keycloak module to enable PKR-Functionality in Keycloak

## Storing User Credentials

### Data Format
For the keycloak implementation, we will store all data in the form of Base64 self describing JOSE objects. This opens the opportunity to store the those informations even in the simplest key value stores.

### Storage Location
The location where we store user credential will be highly dependent on the policy of the company which intends to operate the idp server.

#### Active Directory
Some company store user cedential in LDAP records. In those situations, we can also decide to store thos user credentials as attribute of the directory entry storing the user password.

#### Use Attribute as a Storage
The keycloak user attribute is a bag of key values. We can also decide to store thos user information on this location. This approach bears the advantage, that the there is less interaction between the keycloak server and an internal system like the active directory. Besides this, the caching of active user in keycloak will also allow for faster access to those information.

#### Using another Location
In order to separate the processing of those attribute from data at rest, a company policy might also decide to store the user credentials in a database that is located in another security zone.

#### PKRSUserAttributeProviderSPI
In order to abstract the location where those information are stored, we will introsuce a proper SPI. The SPI could have the folloing interface:
  ```java
	public interface PKRSUserAttributeProvider {
		public void putAttribute(KeycloakSEssion session,String userId, String attributeName, String attributeValue);
		public String getAttribute(KeycloakSEssion session,String userId, String attributeName);
		public String removeAttribute(KeycloakSEssion session,String userId, String attributeName);
	}
  ```

## Generating and Protecting Key Pairs

### PKRKeyGeneratorSPI
This approach will involve a lot of key pair generation. Also in order to allow for an evolutive maintenance of cryptography algorithm, it is necessary to provide a proper SPI to plug in key pair generators into the keycloak environment. 

  ```java
	public interface PKRKeyGenerator {
	    // returns String KeyPair.JWK.Base64
		public String genKeyPair(RandomSource random,String keyAlgo, int keysize); 
	    // returns String SecretKey.JWK.Base64
		public String genSecretKey(RandomSource random,String keyAlgo, int keysize);
		// Used to bind such keypairs to a user id. 
		// This can be used for the generation of a maasterPassword.
	    // returns String SecretKey.JWK.Base64
		public String genSecretKey(char[] seed,String salt, String keyAlgo, int keysize);  
	}
  ```

### Protecting a Key Pair

We need no SPI for this functionality i guess.

  ```java
	public interface PKRKeyProtector {
	    // After encryption, containing class will use the PKRSUserAttributeProviderSPI to store the encrypted keypair in the user attribute.
	    // returns String:EncryptedKeyPair_Main.JWE.Base64
		public String encryptKeyPair(String:KeyPair.JWK.Base64 keyPair,String:MasterPassword.JWK.Base64 masterPassword);
	    // Before decryption, containing class will use the PKRSUserAttributeProviderSPI to load the encrypted keypair in the user attribute.
		// return String:KeyPair.JWK.Base64
		public String decryptKeyPair(String:KeyPair_Main.JWE.Base64 encryptedMasterKey,String:MasterPassword.JWK.Base64 masterPassword);

		public String publicKeyEncryptSecretKey(SecretKey.JWK, PublicKey.JWK) : PublicKeyEncryptedSecretKey.JWE
		public String privateKeyDecryptSecretKey(SecretKey.JWK, PublicKey.JWK) : PrivateKeyDecryptedSecretKey.JWE
	}
  ```
### Overall Workflow
Upon storing the user password for the first time, the idp server will have to go through the following steps:

  ```java
	public void storeUserPassword(KeycloakSEssion session, String userId, char[] password) {
	    // Generate a new keypair
	    String keypair:KeyPair.JWK.Base64 = pkrKeyGenerator.genKeyPair(random:RandomSource, keyAlgo:String, keysize:int);
	    // Generate masterPassowrd.
	    String masterPassword:SecretKey.JWK.Base64 = pkrKeyGenerator.genSecretKey(password:char[],userId:String, keyAlgo:String, keysize:int);
	    // Use the master password to protect the master key pair.
	    String:EncryptedKeyPair_Main.JWE.Base64 encryptedMasterKey = pkrKeyProtector.encryptKeyPair(keyPair:String:KeyPair.JWK.Base64, masterPassword:String:MasterPassword.JWK.Base64);
	    // Store the encrypted master key in the user attribute.
	    pkrSUserAttributeProvider.putAttribute(session:KeycloakSEssion, userId:String, "EncryptedKeyPair_Main.JWE.Base64":String, encryptedMasterKey:String);
       // Generate refresh token keypair record.
	    // Generate generate refresh token secret key
	    String:SecretKey.JWK.Base64 refreshTokenPassword = pkrKeyGenerator.genSecretKey(random:RandomSOurce,keyAlgo:String, keysize:int);
	    // Use the refresh token password to protect the master key pair.
	    String:EncryptedKeyPair_TokenId.JWE.Base64 encryptedMasterKeyRt = pkrKeyProtector.encryptKeyPair(keyPair:String:KeyPair.JWK.Base64, refreshTokenPassword:String:SecretKey.JWK.Base64);
	    // Store the encrypted master key in the user attribute.
	    pkrSUserAttributeProvider.putAttribute(session:KeycloakSEssion, userId:String, "EncryptedKeyPair_TokenId.JWE.Base64":String, encryptedMasterKey:String);
	    // Use the public key of the idp to protect refresh token password.
	    String:PublicKeyEncryptedSecretKey.JWE.Base64 encryptedRefreshTokenPassword_tokenId =  pkrKeyProtector.publicKeyEncryptSecretKey(refreshTokenPassword_tokenId:String:SecretKey.JWK.Base64, idpPublicKey:String:PublicKey.JWK);
	    // hold encrypted refresh token password in the session
	    session.put("encryptedRefreshTokenPassword_tokenId", encryptedRefreshTokenPassword_tokenId);
	}
  ```

While populating the refresh token, the idp will put the encryptedRefreshTokenPassword_tokenId (if possible) into the refresh token under the claim name "pkr_cek" for reuse while creating new token.

### Recovering a Key Pair

In order to recover the key pair, the idp always need an information from the caller. This is generally a password stored in the token.
* If the idp is producing a token from a refresh token, the refresh token will be carrying an idp password.
* If the idp is producing a token from the login of a client, the master password shall still be available in the client session.  

## Generating token for Resource Server 
  
This situation happens, when we know that the resource server need a per user key to protect some sensitive user informations. Since a resource server does not have access to user private credentials, the idp server will have to include some per user secret in the access token addressed to the resource server. We advice here not to have a single credential encryption key that is shared by many resources. It is better to hat one per resource server.

In order to make sure that no other process has access to this credential encryption key, the idp server uses the public key of the resource server to encrypt the credential before putting it in the access token. Overall routine looks like:

  ```java
	public void populateAccessTokenPassword(KeycloakSEssion session) {
	
	    // Resolve resouce server public key 
	    PublicKey.JWK idpPublicKey = pkrPublicKeyResolver.resolvePublicKey(session:KeycloakSession, client:Client);

       // =========== Generate resource server master password. =============
	    // Generate generate resource server master password
	    String:SecretKey.JWK.Base64 resourceServerMasterPassword = pkrKeyGenerator.genSecretKey(random:RandomSOurce,keyAlgo:String, keysize:int);

	    // Use the user public key to encrypt the resource server master key password.
	    String:PublicKeyEncryptedSecretKey.JWE.Base64 encryptedResourceServerPassword_ResId =  pkrKeyProtector.publicKeyEncryptSecretKey(resourceServerMasterPassword_resId:String:SecretKey.JWK.Base64, idpPublicKey:String:PublicKey.JWK);
	    // Store the encrypted resource server password in the user attribute.
	    pkrSUserAttributeProvider.putAttribute(session:KeycloakSEssion, userId:String, "encryptedResourceServerPassword_ResId.JWE.Base64":String, encryptedResourceServerPassword_ResId:String:PublicKeyEncryptedSecretKey.JWE.Base64);
	    // Use the public key of the idp to protect refresh token password.
	    String:PublicKeyEncryptedSecretKey.JWE.Base64 encryptedRefreshTokenPassword_tokenId =  pkrKeyProtector.publicKeyEncryptSecretKey(refreshTokenPassword_tokenId:String:SecretKey.JWK.Base64, idpPublicKey:String:PublicKey.JWK);
	    // hold encrypted refresh token password in the session
	    session.put("encryptedRefreshTokenPassword_tokenId", encryptedRefreshTokenPassword_tokenId);

	    
	    // get encrypted refresh token password from session
	    String:PublicKeyEncryptedSecretKey.JWE.Base64 encryptedRefreshTokenPassword_tokenId = session.get("encryptedRefreshTokenPassword_tokenId");
	    // Use the idp public key recover refresh token password.
	    String:SecretKey.JWK.Base64 refreshTokenPassword_tokenId = pkrKeyProtector.publicKeyDecryptSecretKey ( encryptedRefreshTokenPassword_tokenId:String:PublicKeyEncryptedSecretKey.JWE.Base64, idpPublicKey:String:PublicKey.JWK);
	    // read refresh token encrypted master key from user attribute
	    String:EncryptedKeyPair_TokenId.JWE.Base64 encryptedMasterKeyRt = pkrSUserAttributeProvider.getAttribute(session:KeycloakSEssion, userId:String, "EncryptedKeyPair_TokenId.JWE.Base64");
	    // Use the refresh token password to decrypt the master key pair.
	    String:KeyPair.JWK.Base64 keyPair = pkrKeyProtector.decryptKeyPair (String:EncryptedKeyPair_TokenId.JWE.Base64 encryptedMasterKeyRt, refreshTokenPassword:String:SecretKey.JWK.Base64);
	}
  ```


### Reading the Resource server Public Key
This functionality is provided by default by the keycloak sever. We will have to provide a wrapper class that hides this functionality from the framewrok.

  ```java
	public interface PKRPublicKeyResolver {
	    // For example, fetch resource server key from {baseURI}/k_jwks . e.g.: https://mydomain/context-root/k_jwks
	    // returns String:Public_Key.JWK.Base64
		public String resolvePublicKey(KeycloakSession session, Client client);
	}
  ```

### Overall Workflow
Upon storing the user password for the first time, the idp server will have to go through the following steps:

  ```java
	public void populateAccessTokenPassword(KeycloakSEssion session, String userId, char[] password) {
	    // Generate a new keypair
	    String keypair:KeyPair.JWK.Base64 = pkrKeyGenerator.genKeyPair(random:RandomSource, keyAlgo:String, keysize:int);
	    // Generate masterPassowrd.
	    String masterPassword:SecretKey.JWK.Base64 = pkrKeyGenerator.genSecretKey(password:char[],userId:String, keyAlgo:String, keysize:int);
	    // Use the master password to protect the master key pair.
	    String:EncryptedKeyPair_Main.JWE.Base64 encryptedMasterKey = pkrKeyProtector.encryptKeyPair(keyPair:String:KeyPair.JWK.Base64, masterPassword:String:MasterPassword.JWK.Base64);
	    // Store the encrypted master key in the user attribute.
	    pkrSUserAttributeProvider.putAttribute(session:KeycloakSEssion, userId:String, "EncryptedKeyPair_Main.JWE.Base64":String, encryptedMasterKey:String);
         
       // Generate refresh token keypair record.
	    // Generate generate refresh token secret key
	    String:SecretKey.JWK.Base64 refreshTokenPassword = pkrKeyGenerator.genSecretKey(random:RandomSOurce,keyAlgo:String, keysize:int);
	    // Use the refresh token password to protect the master key pair.
	    String:EncryptedKeyPair_TokenId.JWE.Base64 encryptedMasterKeyRt = pkrKeyProtector.encryptKeyPair(keyPair:String:KeyPair.JWK.Base64, refreshTokenPassword:String:SecretKey.JWK.Base64);
	    // Store the encrypted master key in the user attribute.
	    pkrSUserAttributeProvider.putAttribute(session:KeycloakSEssion, userId:String, "EncryptedKeyPair_TokenId.JWE.Base64":String, encryptedMasterKey:String);
	    
	    // Use the public key of the idp to protect refresh token password.
	    String:PublicKeyEncryptedSecretKey.JWE.Base64 encryptedRefreshTokenPassword_tokenId =  pkrKeyProtector.publicKeyEncryptSecretKey(refreshTokenPassword_tokenId:String:SecretKey.JWK.Base64, idpPublicKey:String:PublicKey.JWK);
	    
	    // hold encrypted refresh token password in the session
	    session.put(userid, encryptedRefreshTokenPassword_tokenId);
	}
  ```


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
