package org.adorsys.tmjv.token;

import com.nimbusds.jose.jwk.JWK;

/**
 * This interface represents a JWT confimatio claim.
 * 
 * See rfc7800
 * 
 * @author fpo
 *
 */
public class JwtCnf {
	private JWK jwk;
	private String jku;
	private String kid;

	/**
	 * When the key held by the presenter is an asymmetric private key, the
	 * "jwk" member is a JSON Web Key [JWK] representing the corresponding
	 * asymmetric public key.  The following example demonstrates such a
	 * declaration in the JWT Claims Set of a JWT:
	 * 
	 *   {
	 *    "iss": "https://server.example.com",
	 *    "aud": "https://client.example.org",
	 *    "exp": 1361398824,
	 *    "cnf":{
	 *      "jwk":{
	 *        "kty": "EC",
	 *        "use": "sig",
	 *        "crv": "P-256",
	 *        "x": "18wHLeIgW9wVN6VD1Txgpqy2LszYkMf6J8njVAibvhM",
	 *        "y": "-V4dS4UaLMgP_4fY4j8ir7cl1TXlFdAgcx55o7TkcSA"
	 *       }
	 *     }
	 *   }
	 *   
	 * The JWK MUST contain the required key members for a JWK of that key
	 * type and MAY contain other JWK members, including the "kid" (Key ID)
	 * member.
	 * 
	 * The "jwk" member MAY also be used for a JWK representing a symmetric
	 * key, provided that the JWT is encrypted so that the key is not
	 * revealed to unintended parties.  The means of encrypting a JWT is
	 * explained in [JWT].  If the JWT is not encrypted, the symmetric key
	 * MUST be encrypted as described below.
	 * @return
	 */
	public JWK getJwk(){
		return jwk;
	}
	
	/**
	 * The proof-of-possession key can be passed by reference instead of
	 * being passed by value.  This is done using the "jku" member.  Its
	 * value is a URI [RFC3986] that refers to a resource for a set of JSON-
	 * encoded public keys represented as a JWK Set [JWK], one of which is
	 * the proof-of-possession key.  If there are multiple keys in the
	 * referenced JWK Set document, a "kid" member MUST also be included
	 * with the referenced key's JWK also containing the same "kid" value.
	 * 
	 * The protocol used to acquire the resource MUST provide integrity
	 * protection.  An HTTP GET request to retrieve the JWK Set MUST use TLS
	 * [RFC5246] and the identity of the server MUST be validated, as per
	 * Section 6 of RFC 6125 [RFC6125].
	 * 
	 * The following example demonstrates such a declaration in the JWT
	 * Claims Set of a JWT:
	 * 
	 *   {
	 *    "iss": "https://server.example.com",
	 *    "sub": "17760704",
	 *    "aud": "https://client.example.org",
	 *    "exp": 1440804813,
	 *    "cnf":{
	 *      "jku": "https://keys.example.net/pop-keys.json",
	 *      "kid": "2015-08-28"
	 *     }
	 *   }
	 * @return
	 */
	public String getJku(){
		return jku;
	}
	
	/**
	 * The proof-of-possession key can also be identified by the use of a
	 * Key ID instead of communicating the actual key, provided the
	 * recipient is able to obtain the identified key using the Key ID.  In
	 * this case, the issuer of a JWT declares that the presenter possesses
	 * a particular key and that the recipient can cryptographically confirm
	 * proof of possession of the key by the presenter by including a "cnf"
	 * claim in the JWT whose value is a JSON object with the JSON object
	 * containing a "kid" member identifying the key.
	 *    
	 * The following example demonstrates such a declaration in the JWT
	 * Claims Set of a JWT:
	 * 
	 *   {
	 *    "iss": "https://server.example.com",
	 *    "aud": "https://client.example.org",
	 *    "exp": 1361398824,
	 *    "cnf":{
	 *      "kid": "dfd1aa97-6d8d-4575-a0fe-34b96de2bfad"
	 *     }
	 *   }
	 *   
	 * The content of the "kid" value is application specific.  For
	 * instance, some applications may choose to use a JWK Thumbprint
	 * [JWK.Thumbprint] value as the "kid" value.
	 * 
	 * @return
	 */
	public String getKid(){
		return kid;
	}
}
