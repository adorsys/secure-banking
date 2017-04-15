# Encrypted Object Service

This module provide an interface and cryptographic routines to store files encrypted. For efficient storage and recovery of those files, this framework manages to artifacts for each file to be stored:
* The gaurd file, which manages metainformation on the file to be stored
* The payload file, which hols the encrypted payload.
  
## Data Guard

The data guard contains meta informations of the file to be stored. These are generally:
* The key used to encrypt the data file
* A compression pattern is applicable
* The handle to the storage location of the file
* The size of the original file if available
* The checksum of the original file if available
* Limited application specific description of the file. 

## Data Payload
The data payload is an encrypted output of the file.

## Storing the file

Before storing the file, 
A client :
First sends a file storage request to the server. This request contains following information
* The size of the file to be stored
* The checksum of the file to be stored
* The compression pattern that can be used to reduce the file size
* The limited application specific description of the file.
  
The Server upon receiving this request:
* Generates a key that will used to encrypt the data file and put it in the file guard
* Use client credentials to encrypt and store the file guard in a persistent storage 
* Produces a file handle that is sent back to the client.

The file handle contains
* The external persistent reference to the file
* A cryptographic key that must be used to encrypt the file
* A the compression pattern to be used to compress the file
* The storage location of the file

Upon receiving the file handle the client:
* The client streams the file compressed and encrypted to the file storage
* The client streams the file raw to the server and the server streams the file compressed and encrypted to the file storage.

## Reading the file
The client sends a read request to the server containing
* The external persistent file handle
* Client credentials

The Server:
* Retrieves the file guard using client credentials
* If the client wishes for server side decryption, the server streams back the decrypted file to the client.
* If the client wishes for client side decryption, the server returns a file handle to the client containing
  * A cryptographic key that must be used to decrypt the file
  * A the compression pattern to be used to decompress the file
  * The storage location of the file payload
