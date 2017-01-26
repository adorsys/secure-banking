# CSE Java Keycloak

The intention of this module is to provide CSE implementations as a keycloak extension.

## TODOs

### Define and implements JWTPublicKeyIncluder
    * JWTPublicKeyIncluder
      * include(oAuthToken JWT, resourceServerPublicKey JWK): JWT
      * The name of the claim to include is "res_pub_key"

