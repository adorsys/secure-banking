package org.adorsys.encobject.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="ObjectHandle", description="Contains information necessary to storage or retrieval an object from the object storage.")
public class ObjectHandle {
	
	private String container;

	private String name;

	public ObjectHandle() {
		super();
	}

	public ObjectHandle(String container, String name) {
		super();
		this.container = container;
		this.name = name;
	}

	@ApiModelProperty(value = "The name of the container in qhich the object is stored")
	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	@ApiModelProperty(value = "The name of the object in the store")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
