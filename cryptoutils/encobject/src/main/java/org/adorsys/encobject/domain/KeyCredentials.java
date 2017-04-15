package org.adorsys.encobject.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="KeyCredentials", description="Contains information necessary to retrieve a key from a key store. The type or format of the key store is not relevant for this interface.")
public class KeyCredentials {
	
	private String storeid;
	
	private String storepass;
	
	private String keyid;
	
	private String keypass;

    @ApiModelProperty(value = "The id of the key store in the realm")
	public String getStoreid() {
		return storeid;
	}

	public void setStoreid(String storeid) {
		this.storeid = storeid;
	}

    @ApiModelProperty(value = "The password used to access the keystore.")
	public String getStorepass() {
		return storepass;
	}

	public void setStorepass(String storepass) {
		this.storepass = storepass;
	}

    @ApiModelProperty(value = "This is the id of a key stored in the keystore. In Java key stores, this is generally the key alias.")
	public String getKeyid() {
		return keyid;
	}

	public void setKeyid(String keyid) {
		this.keyid = keyid;
	}

    @ApiModelProperty(value = "This is the password used to retrieve the key. This information can be left blank when the operation is just intending to read the certificate associated with the corresponding keyid")
	public String getKeypass() {
		return keypass;
	}

	public void setKeypass(String keypass) {
		this.keypass = keypass;
	}

}
