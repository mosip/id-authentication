package io.mosip.registrationprocessor.mosip_regprocessor_rest_client.dto;

import java.io.Serializable;

public class RequestDetails implements Serializable{
	private static final long serialVersionUID = -5549629875402269382L;
	
	
	private String requestMethodType;
	private String requestUrl;
	
	
	public String getRequestMethodType() {
		return requestMethodType;
	}
	public void setRequestMethodType(String requestMethodType) {
		this.requestMethodType = requestMethodType;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	

}
