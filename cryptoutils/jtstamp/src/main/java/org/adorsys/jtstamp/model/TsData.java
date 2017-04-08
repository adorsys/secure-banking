package org.adorsys.jtstamp.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="Holds a time stamp request", value="TsData")
public class TsData {
	private String sub;
	private String oid;
	private String halg;
	private String hval;
    
	private boolean inclIss = true;
	private boolean inclKid = true;
	
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

    @ApiModelProperty(required=true, value = "The \"inclIss\" (include issuer) claim indicates if the issuer schould be included timestamp object.  Use of this claim is OPTIONAL. Default is true")
	public boolean isInclIss() {
		return inclIss;
	}

	public void setInclIss(boolean inclIss) {
		this.inclIss = inclIss;
	}

    @ApiModelProperty(required=true, value = "The \"inclKid\" (include key identifier) claim indicates if the identifier of the public key used to sign the claim should be included in the timestamp.  Use of this claim is OPTIONAL. Default is true")
	public boolean isInclKid() {
		return inclKid;
	}

	public void setInclKid(boolean inclKid) {
		this.inclKid = inclKid;
	}
}
