# CSE Java Keycloak

The intention of this module is to provide CSE implementations as a keycloak extension.

## TODOs
* Create an IDP extension which uses JWTPublicKeyIncluder component to include resource server public key into oAuthToken
* Create an IDP extension to decrypt secret credentials sent to IDP during logon workflow

Following are the steps to be used for the implementation of a keycloak prototype.
* Provide poms for the deployment of a default keycloak server
* Provide protocol mapper for the inclusion of the public key of the server into the refresh token.

