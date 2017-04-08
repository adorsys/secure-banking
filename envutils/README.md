# Env Utils

Utility components for reading properties from environment or system properties in client or server environment.

This module will address issues associated with:

## Retrieving Simple Properties from System or Environment

* Retrieving a property information from the system environment (in the case os Java: System.getEnv(...))
* Retrieving a property information from the system properties (in the case of java System.getProperty(...))
* Retrieving a property information from the system environment or from the system properties ( EnvUtils.getEnvOrSysProperty())
* Retrieving a property information from the system properties or from the system environment ( EnvUtils.getSysOrEnvProperty())

## Retrieving Encrypted Properties from System or Environment

* Use password=jwe(pswd) to retrieve a json encrypted password.