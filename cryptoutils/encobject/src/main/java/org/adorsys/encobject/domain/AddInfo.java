package org.adorsys.encobject.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="AddInfo", description="Additional content meta information. Will not be saved encrypted")
public class AddInfo {

	@ApiModelProperty(value = "The entry name")
	private String name;
	@ApiModelProperty(value = "The entry value")
	private String value;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
