# Private Key Recovery

## The Problem 

We are working on a lot of processes involving the processing and storage of critical data by computer systems. Most of these processes involve the possession of a key pair by each participant. Sample operation involving or requiring the ownership of keys are:

### The possession of a crypto currency
Most crypto currency realms require the user to own a key pair. The user can share his public key to receive payment, the user must use the private key to make payment. 

### Secure messaging 
A secure messaging environment will also require the user to own a key pair. This is the case with pgp and smime based email.

### Secure data storage
Individual encryption of critical user data in the data center is an even more prominent issue as more and more data leak often lead to disclosure user data like credit card data and personal identification numbers.  

### Makes cryptographic key critical
There is even more stuff moving in the direction of electronic data processing. The use of cryptographic keys becoming more critical than ever.

## Purpose 
The main purpose of this work is to combine techniques and processes to provide a way to recover from lost private key, while keeping them as private as they are today.
 
* If the missing key pair was used to hold a crypto currency, the money is lost.
* If the missing key pair was used to involve into secure messaging, older messages can not be read anymore.
* If the missing key pair was used to protect some data, access to that data is gone. 

Observation of the crypto based tool market shows that the securing of the private key is left to the owner of the key pair. This strongly limits the extent of user who have access to these technology. A computer savy person might backup his key pair in a way to prevent access form malicious entities. This is not the case of a common person.

The main problem associated with the possession of a key pair is that if the user loses access to his private key, all information protected by that key pair can not be used. 

## Trusted Party

The core idea of this work build on the notion of trust. I want to give part of my key to someone I trust or put it into something I control.

A trusted party is a party I trust. 
* A party that defines some processes used to identify me.
* A party I can give some piece of information to and be sure that he will only give it back to me after proper identification.

### Institutions as Trusted Parties

* The bank that holds my bank account
* The department of driver license of my county
* My insurance company
* My attorney
* My email provider (like google)
* My telecom provider

### People as Trusted Parties

But trusted parties must not always be institutions, they can also be:
* My wife
* My mom
* my friend

### Devices as Trusted Parties

My trusted parties can also just be devices I own. In this case, these devices will be used to protect each order.

A difference between an institution and people are:
* An institution provides a cleaner identification process
* An institution can provide more reliable data storage to keep trustee key data
* A person bearing this role will have to find a solution for the recovery of key data entrusted to him. This can be:
  * His drop box account
  * His email account
  Beware that that those entrusted key data are well encrypted and can not be used by the storage provider anyway.  


## Sample Workflow

It all turns around splitting and sharing private key (Master Key) to trusted parties
* In order to backup the key pair, the owner splits the private key and encrypts each chunk with the public key of the corresponding trusted party.
  * The public key encrypted chunk can be sent to the trusted party for storage.
    * This approach bears the risk, that data are out of the user's control.
    * But there is no extra backup need by the user. 
  * The public key encrypted chunk can be stored by the user and send to the trusted party when the user need recovery.
    * This approach bears the risk that the user might renew his key and make our key protection useless.
    * Then a notification mechanism will have to be implemented to allow for update of key chunk when trusted party public key changes.

In order to manage revocation, it might be a better idea to directly send the encrypted chunk of the master key to the trusted party. An indirection can allow us to generate a secret key (Master Key Encryption Key) that is used to encrypt the key pair (Master Key) 

* We will have to backup the encrypted master key and 
* Chunk and public key encrypt the "Master Key Encryption Key" with trusted's public key.
* We can also use the backup record to store information on how the key protection process occurred.

With the indirection, a valid combination of trusted can not access the master key without holding the "Encrypted Master Key Record".

## Master Key

The master key is a key pair that we use to perform a cryptographic task. Mostly identifying tasks.

### Generating the Master Key

The master key shall normally be generated by the system that uses the private key. In short, the private key should never leave the application that generates it.

* If for example the master key is used to manage other keys on an idp server, the master key shall be generated by the idp server. 
* If instead the master key is used manage a crypto currency wallet, the key pair shall be generated by the crypto wallet software.

### Recovering the Master Key

The master key must also be recovered by the application that uses the private key. If the key using system does not have a database, the system can be connected to standard data storage system like emails, dropbox... 

### Protecting the Master Key
* Generate and maintain a key pair for an entity (Storing the key on the key using device).
* Prevent any access to that key without interaction of the user (Master Key Encryption Key)
  * The simplest case of a master key encryption key is a key derived from the user password.
    * This approach is appropriate for keys leaving on the user device.
  * If the key is stored on the server, the client device will have a master key encryption key 
    * That is generated and stored locally
    * That is protected by a user device local password
  * If a key is stored on the server and the application design does not want to have dependency on local devices, 
    * a key derivation function must be define for partial hashing of the password
    * The real clear text user password will never leave the the local user device.  
* Provide a way of recovering the key when the user has lost any handle to the key (Trusted Party)

### How do we use the key pair?
* The key pair generating application wild never release the private key of an entity to an external process. Instead, that application will
  * Perform cryptographic functions using the private key,
  * Manage recovery operations whenever necessary to get back to a new version of that key.

The main problem we still have with this approach is that if the user loses this key, there won't be any way or recovering his master key.

### How do we recover from the a lost master key
* For a given combination of trusted parties
  * We generate a recovery key pair (rk) and send the rk-encryption key to the user.
  * We send a recovery request to trusted each party. When the trusted party logs in to help his trustee, we do the following
    * We use the master key of the trusted party to decrypt the chunk concerned
    * We use the public key of the rk to encrypt the decrypted chunk.
  * As soon as all involved trusted party have approved recovery
    * A notification is sent to the user.
    * The user uses private key of the recovery encryption key to regain access to his master key.
    * From there user can define a new master key encryption key.
 
# Formal Description of the Framework.

## Managing Public Keys
This framework does not define how public keys are mapped to their owner, neither does the framework set some constraints or such operation. We assume that higher lever applications can have something like a contact management system or a publicKey/email pairs identifying their owners. SOme other systems can also provide directories where user attributes are mapped to their public keys. 

## Key Management
  
### Key Generator
 The key generator is a component that resides in the key using application. A key generator generates the key pair need by an application and encrypts it with a user provided password. This always exists as a standard feature in most crypto tools. The key generator will have te following abstract interface:
  * generateKey(KeySpec): BinaryKeyPair or KeyPair.JWK
    programming language specific representation.
  * protectKeyPair(userPassword): EncryptedKeypair_Main.JWE
    We can use a JWE Format to make more descriptive.
  * storeOnLocalDevice(EncryptedKeypair_Main.JWE)
    The local device here can be the keychain, the database or the file system.
 
### Key Protector
The purpose of the key protector is to generate other key stores with the same key, but other "passwordDerivedKey"s. This runs like:

#### Clone the Keystore with a new secret key 
  * generateNewSecretKeyFromRandom(KeySpec, RandomSource):NewSecretKey_Backup0.JWK
     Representation of new secret key can be self contained in a JWK.
  * createNewKeyStoreFrom(EncryptedKeypair_Main.JWE,userPassword, NewSecretKey_Backup0.JWK):EncryptedKeypair_Backup0.JWE
  * backupOnRemoteDevice(EncryptedKeypair_Backup0.JWE)
    The remote device can be an email folder, dropbox. For a server application the remote device could be the active organizations LDAP server or a database in another security zone.
   
#### Splitt the NewSecretKey_Backup0.JWK to many public keys
  * splittSecretKey(NewSecretKey_Backup0, PublicKeys.JWK[]):PublicKeyEncryptedChunks_Backup0.JWE[]
  * backupOnRemoteDevice(PublicKeyEncryptedChunks_Backup0.JWE[])
    The remote device can be an email folder, dropbox. For a server application the remote device could be the active organizations LDAP server or a database in another security zone.

   