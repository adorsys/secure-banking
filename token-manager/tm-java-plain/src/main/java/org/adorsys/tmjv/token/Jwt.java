package org.adorsys.tmjv.token;

import java.util.Date;
import java.util.HashMap;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="Holds an access token request", value="Jwt")
public class Jwt {
    private HashMap<String, String> headers = new HashMap<>();
    private String iss;
    private String sub;
    private String aud;
    private Date exp;
    private Date nbf;
    private Date iat;
    private String jti;
    private JwtCnf cnf;

    /**
     * Returns a header param included in the Jwt
     *
     * @param name
     * @return
     */
    public String getHeader(String name) {
        return headers.get(name);
    }

    /**
     * Returns the issuer of this JWT.
     * The "iss" (issuer) claim identifies the principal that issued the
     * JWT.  The processing of this claim is generally application specific.
     * The "iss" value is a case-sensitive string containing a StringOrURI
     * value.  Use of this claim is OPTIONAL.
     *
     * @return
     */
	@ApiModelProperty(required=true, value = "Returns the issuer of this JWT. The \"iss\" (issuer) claim identifies the principal that issued the JWT.  The processing of this claim is generally application specific. The \"iss\" value is a case-sensitive string containing a StringOrURI value.  Use of this claim is OPTIONAL.")
    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    /**
     * The "sub" (subject) claim identifies the principal that is the
     * subject of the JWT.  The claims in a JWT are normally statements
     * about the subject.  The subject value MUST either be scoped to be
     * locally unique in the context of the issuer or be globally unique.
     * The processing of this claim is generally application specific.  The
     * "sub" value is a case-sensitive string containing a StringOrURI
     * value.  Use of this claim is OPTIONAL.
     *
     * @return
     */
    @ApiModelProperty(required=true, value = "The \"sub\" (subject) claim identifies the principal that is the subject of the JWT.  The claims in a JWT are normally statements about the subject.  The subject value MUST either be scoped to be locally unique in the context of the issuer or be globally unique. The processing of this claim is generally application specific.  The \"sub\" value is a case-sensitive string containing a StringOrURI value.  Use of this claim is OPTIONAL.")
        public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    /**
     * The "aud" (audience) claim identifies the recipients that the JWT is
     * intended for.  Each principal intended to process the JWT MUST
     * identify itself with a value in the audience claim.  If the principal
     * processing the claim does not identify itself with a value in the
     * "aud" claim when this claim is present, then the JWT MUST be
     * rejected.  In the general case, the "aud" value is an array of case-
     * sensitive strings, each containing a StringOrURI value.  In the
     * special case when the JWT has one audience, the "aud" value MAY be a
     * single case-sensitive string containing a StringOrURI value.  The
     * interpretation of audience values is generally application specific.
     * Use of this claim is OPTIONAL.
     *
     * @return
     */
    @ApiModelProperty(value = "The \"aud\" (audience) claim identifies the recipients that the JWT is intended for.  Each principal intended to process the JWT MUST identify itself with a value in the audience claim.  If the principal processing the claim does not identify itself with a value in the \"aud\" claim when this claim is present, then the JWT MUST be rejected.  In the general case, the \"aud\" value is an array of case- sensitive strings, each containing a StringOrURI value.  In the special case when the JWT has one audience, the \"aud\" value MAY be a single case-sensitive string containing a StringOrURI value.  The interpretation of audience values is generally application specific. Use of this claim is OPTIONAL.")
    public String getAud() {
        return aud;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    /**
     * The "exp" (expiration time) claim identifies the expiration time on
     * or after which the JWT MUST NOT be accepted for processing.  The
     * processing of the "exp" claim requires that the current date/time
     * MUST be before the expiration date/time listed in the "exp" claim.
     * Implementers MAY provide for some small leeway, usually no more than
     * a few minutes, to account for clock skew.  Its value MUST be a number
     * containing a NumericDate value.  Use of this claim is OPTIONAL.
     *
     * @return
     */
    @ApiModelProperty(required=true, value = "The \"exp\" (expiration time) claim identifies the expiration time on or after which the JWT MUST NOT be accepted for processing. The processing of the \"exp\" claim requires that the current date/time MUST be before the expiration date/time listed in the \"exp\" claim. Implementers MAY provide for some small leeway, usually no more than a few minutes, to account for clock skew.  Its value MUST be a number containing a NumericDate value.  Use of this claim is OPTIONAL.")
    public Date getExp() {
        return exp;
    }

    public void setExp(Date exp) {
        this.exp = exp;
    }

    /**
     * The "nbf" (not before) claim identifies the time before which the JWT
     * MUST NOT be accepted for processing.  The processing of the "nbf"
     * claim requires that the current date/time MUST be after or equal to
     * the not-before date/time listed in the "nbf" claim.  Implementers MAY
     * provide for some small leeway, usually no more than a few minutes, to
     * account for clock skew.  Its value MUST be a number containing a
     * NumericDate value.  Use of this claim is OPTIONAL.
     *
     * @return
     */
    @ApiModelProperty(value = "The \"nbf\" (not before) claim identifies the time before which the JWT MUST NOT be accepted for processing.  The processing of the \"nbf\" claim requires that the current date/time MUST be after or equal to the not-before date/time listed in the \"nbf\" claim.  Implementers MAY provide for some small leeway, usually no more than a few minutes, to account for clock skew. Its value MUST be a number containing a NumericDate value. Use of this claim is OPTIONAL.")
    public Date getNbf() {
        return nbf;
    }

    public void setNbf(Date nbf) {
        this.nbf = nbf;
    }

    /**
     * The "iat" (issued at) claim identifies the time at which the JWT was
     * issued.  This claim can be used to determine the age of the JWT.  Its
     * value MUST be a number containing a NumericDate value.  Use of this
     * claim is OPTIONAL.
     *
     * @return
     */
    @ApiModelProperty(required=true, value = "The \"iat\" (issued at) claim identifies the time at which the JWT was issued. This claim can be used to determine the age of the JWT. Its value MUST be a number containing a NumericDate value. Use of this claim is OPTIONAL.")
    public Date getIat() {
        return iat;
    }

    public void setIat(Date iat) {
        this.iat = iat;
    }

    /**
     * The "jti" (JWT ID) claim provides a unique identifier for the JWT.
     * The identifier value MUST be assigned in a manner that ensures that
     * there is a negligible probability that the same value will be
     * accidentally assigned to a different data object; if the application
     * uses multiple issuers, collisions MUST be prevented among values
     * produced by different issuers as well.  The "jti" claim can be used
     * to prevent the JWT from being replayed.  The "jti" value is a case-
     * sensitive string.  Use of this claim is OPTIONAL.
     *
     * @return
     */
    @ApiModelProperty(value = "The \"jti\" (JWT ID) claim provides a unique identifier for the JWT. The identifier value MUST be assigned in a manner that ensures that there is a negligible probability that the same value will be accidentally assigned to a different data object; if the application uses multiple issuers, collisions MUST be prevented among values produced by different issuers as well.  The \"jti\" claim can be used to prevent the JWT from being replayed. The \"jti\" value is a case- sensitive string. Use of this claim is OPTIONAL.")
    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    /**
     * The confirmation claim.
     *
     * @return
     */
    @ApiModelProperty(value = "By including a \"cnf\" (confirmation) claim in a JWT, the issuer of the JWT declares that the presenter possesses a particular key and that the recipient can cryptographically confirm that the presenter has possession of that key. The value of the \"cnf\" claim is a JSON object and the members of that object identify the proof-of-possession key. \nThe presenter can be identified in one of several ways by the JWT depending upon the application requirements. If the JWT contains a \"sub\" (subject) claim [JWT], the presenter is normally the subject identified by the JWT. (In some applications, the subject identifier will be relative to the issuer identified by the \"iss\" (issuer) claim [JWT].) If the JWT contains no \"sub\" claim, the presenter is normally the issuer identified by the JWT using the \"iss\" claim. The case in which the presenter is the subject of the JWT is analogous to Security Assertion Markup Language (SAML) 2.0.")
    public JwtCnf getCnf() {
        return cnf;
    }

    public void setCnf(JwtCnf cnf) {
        this.cnf = cnf;
    }
}
