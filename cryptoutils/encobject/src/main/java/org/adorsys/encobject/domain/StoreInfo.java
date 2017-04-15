package org.adorsys.encobject.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="StoreInfo", description="Contains information necessary to storage or retrieval an object.")
public class StoreInfo {

	private String handle;

	@ApiModelProperty(value = "Url that can be called to upload the object.")
	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}
}
