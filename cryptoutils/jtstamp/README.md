# Time Stamping Service

Simple time stamping service. This module consumes a hash value and returns a jws object containing that value, this signature time and the signature value. Any process with access to corresponding the public key can verify the authenticity of the given hash value.

This routine does not guaranty the authenticity of the base data, it only guaranty that the caller was in possession of the hash value at the signature time.

Additional information like the hash algorithm can be stored in the object.

## Data Specification

"halg" : stands for the hash algorithm used to compute the hash value being signed.

"hval" : the hash value of the base data.

Example of client provided info:

We assume the base data to encrypt is "francis loves crypto"

```
{ 
  "sub":"francis", // optional 
  "oid":"dfasdfasdf", // optional
  "halg":"SHA1",
  "hval":"e43e33f5b3c0b1ae50d0fadb09dcfffb6e1395d3",
  "inclIss":false,
  "inclKid":false
}
```

or for SHA256

```
{ 
  "sub":"francis", // optional 
  "oid":"dfasdfasdf", // optional
  "halg":"SHA256",
  "hval":"130ae9959f2b11f6defca9bd4e4e5bdee12f709575d500acb62fde0a701123f9",
  "inclIss":false,
  "inclKid":false
}
```

In the result, we will have following fields

```
{ 
  "sub":"francis", // optional 
  "oid":"dfasdfasdf", // optional
  "halg":"SHA256",
  "hval":"130ae9959f2b11f6defca9bd4e4e5bdee12f709575d500acb62fde0a701123f9",
  "iat": 1491642722,
  "iss":"http://localhost:8080/v1/timestamp", // server configured
  "kid":"lkjlh",
  "kidAlg":"SHA256",
}
```
