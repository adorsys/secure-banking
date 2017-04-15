package org.adorsys.encobject.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="ObjectInfo", description="Contains information necessary to manage encrypted storage of an object.")
public class ObjectInfo {
	
	private Long size  = 0l;
	
	private String checksum;
	
	private String compAlg;
	
	private String encAlg;
	
	private String desc;

    @ApiModelProperty(value = "The size of the object to be stored.")
	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

    @ApiModelProperty(value = "The checksum of the object to be stored if available.")
	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

    @ApiModelProperty(value = "The desired compression algorithm.")
	public String getCompAlg() {
		return compAlg;
	}

	public void setCompAlg(String compAlg) {
		this.compAlg = compAlg;
	}

    @ApiModelProperty(value = "The desired encryption algo")
	public String getEncAlg() {
		return encAlg;
	}

	public void setEncAlg(String encAlg) {
		this.encAlg = encAlg;
	}

    @ApiModelProperty(value = "The optional descritpion of the object")
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((checksum == null) ? 0 : checksum.hashCode());
		result = prime * result + ((compAlg == null) ? 0 : compAlg.hashCode());
		result = prime * result + ((desc == null) ? 0 : desc.hashCode());
		result = prime * result + ((encAlg == null) ? 0 : encAlg.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjectInfo other = (ObjectInfo) obj;
		if (checksum == null) {
			if (other.checksum != null)
				return false;
		} else if (!checksum.equals(other.checksum))
			return false;
		if (compAlg == null) {
			if (other.compAlg != null)
				return false;
		} else if (!compAlg.equals(other.compAlg))
			return false;
		if (desc == null) {
			if (other.desc != null)
				return false;
		} else if (!desc.equals(other.desc))
			return false;
		if (encAlg == null) {
			if (other.encAlg != null)
				return false;
		} else if (!encAlg.equals(other.encAlg))
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ObjectInfo [size=" + size + ", checksum=" + checksum + ", compAlg=" + compAlg + ", encAlg=" + encAlg
				+ ", desc=" + desc + "]";
	}
	
	
}
