package org.adorsys.encobject.domain;

import java.util.Date;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="ContentMetaInfo", description="Contains content meta information generally used to enrich and optimize object storage.")
public class ContentMetaInfo {
	
	private String contentTrype;
	private String zip;
	private Date exp;
	private Map<String, Object> addInfos;
	
	@ApiModelProperty(value = "The content mime type")
	public String getContentTrype() {
		return contentTrype;
	}
	public void setContentTrype(String contentTrype) {
		this.contentTrype = contentTrype;
	}
	@ApiModelProperty(value = "The compression algorithm. If not provided, the server will use the content type to decide if to compress the file")
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	@ApiModelProperty(value = "The expiration date of this entry")
	public Date getExp() {
		return exp;
	}
	public void setExp(Date exp) {
		this.exp = exp;
	}
	
	@ApiModelProperty(value = "Additional content meta information. Will not be saved encrypted")
	public Map<String, Object> getAddInfos() {
		return addInfos;
	}
	public void setAddInfos(Map<String, Object> addInfos) {
		this.addInfos = addInfos;
	}
}
