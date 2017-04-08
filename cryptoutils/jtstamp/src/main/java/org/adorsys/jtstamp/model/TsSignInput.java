package org.adorsys.jtstamp.model;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="Holds a time stamp request", value="TsData")
public class TsSignInput {
	private String iss;
	private Date iat;
	private String kid;
	private String kidAlg;

	private String sub;
	private String oid;
	private String halg;
	private String hval;

	@ApiModelProperty(value = "The \"kid\" (key identifier) is the hash value of the public key used to sign this claim. Use of this claim is OPTIONAL.")
	public String getKid() {
		return kid;
	}

	public void setKid(String kid) {
		this.kid = kid;
	}

	@ApiModelProperty(value = "The \"kidAlg\" (key identifier algorithm) is the hash function used to generate the key id. Use of this claim is OPTIONAL.")
	public String getKidAlg() {
		return kidAlg;
	}

	public void setKidAlg(String kidAlg) {
		this.kidAlg = kidAlg;
	}

	@ApiModelProperty(value = "The \"oid\" (object identifier) references the origine data. Use of this claim is OPTIONAL.")
    public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

    @ApiModelProperty(value = "The \"halg\" (hash algorithm) identifies the algorithm used to hash the origine data. Use of this claim is REQUIRED.")
    public String getHalg() {
		return halg;
	}

	public void setHalg(String halg) {
		this.halg = halg;
	}

	public String getHval() {
		return hval;
	}

    @ApiModelProperty(value = "The \"hval\" (hash value) Ist the value being sigend. Use of this claim is REQUIRED.")
	public void setHval(String hval) {
		this.hval = hval;
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
	@ApiModelProperty(required=true, value = "Returns the issuer of this token. The \"iss\" (issuer) claim identifies the principal that issued the token.  The processing of this claim is generally application specific. The \"iss\" value is a case-sensitive string containing a StringOrURI value.  Use of this claim is OPTIONAL.")
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
}
