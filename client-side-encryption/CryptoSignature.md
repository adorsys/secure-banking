# Cryptographic Signatures
This is a simple overview on cryptographic signatures. Here we can distinguish between 3 main purpose for the receiver of a message:
* Integrity
  Recipient want to be confident has not been accidentally changed
* Authentication
  Recipient want to be confident message originates fron sender
* Non Repudiation
  Recipient or any one else can verify message originated from the sender. 
  
Follows a mapping of security features of those crypto signatures:

| Cryptographic Primitive   | Hash 		| Mac					| Digital Signature 	|
| Security Goal				|			|						|						|
|---------------------------|-----------|-----------------------|-----------------------|
| Integrity					| Yes		| Yes					| Yes					|
| Authentication			| No		| Yes					| Yes					|	 
| Non-repudiation			| No		| No					| Yes					|
| Kind of keys				| none		| Symmetric (SecretKey) | Asymmetric (Keypair) 	|
