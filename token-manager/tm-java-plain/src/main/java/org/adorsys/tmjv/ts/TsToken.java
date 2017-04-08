package org.adorsys.tmjv.ts;

import org.adorsys.jtstamp.model.TsSignInput;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;

@JsonInclude(Include.NON_NULL)
@ApiModel(description="Holds a timestamp response", value="TsToken")
public class TsToken {
	
    private String timestamp;
    private TsSignInput responseModel;

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public TsSignInput getResponseModel() {
		return responseModel;
	}

	public void setResponseModel(TsSignInput responseModel) {
		this.responseModel = responseModel;
	}
}
