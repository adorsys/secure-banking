# Java JWK

Utilities to manipulate Json Web Keys. 

Actually load a key pair from a keystore, given following environment properties:
see: org.adorsys.jjwt.ServerKeysProducerUtils

```
String keyStoreFile = EnvProperties.getEnvOrSysProp("SERVER_KEY_STORE_FILE", false);
String storeType = EnvProperties.getEnvOrSysProp("SERVER_KEY_STORE_TYPE", false);
char[] keyStorePassword = EnvProperties.getEnvOrSysProp("SERVER_KEY_STORE_PASSWORD", false).toCharArray();
char[] privateKeysPassword = EnvProperties.getEnvOrSysProp("SERVER_KEY_ENTRY_PASSWORD", false).toCharArray();

```

The resulting object org.adorsys.jjwt.ServerKeysHolder can hold a set of private and public keys.