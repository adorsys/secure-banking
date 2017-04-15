package org.adorsys.encobject.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="EncInfo", description="Contains information necessary to symetrically encrypt or decrypt an object.")
public class EncInfo {

	private String compAlg;
	
	private String encAlg;
	
	private String encKey;

    @ApiModelProperty(value = "The desired compression algorithm.")
	public String getCompAlg() {
		return compAlg;
	}

	public void setCompAlg(String compAlg) {
		this.compAlg = compAlg;
	}

    @ApiModelProperty(value = "The desired encryption algorithm")
	public String getEncAlg() {
		return encAlg;
	}

	public void setEncAlg(String encAlg) {
		this.encAlg = encAlg;
	}

	@ApiModelProperty(value = "The symetric encryption key")
    public String getEncKey() {
		return encKey;
	}

	public void setEncKey(String encKey) {
		this.encKey = encKey;
	}
}
