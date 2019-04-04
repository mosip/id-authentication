package com.mindtree.camel_bridge;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageDto {
	
	private String rid;
	private boolean isValid;
	private String requestType;
	
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}
	
	@JsonProperty(value = "isValid")
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public String isRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	@Override
	public String toString() {
		return "MessageDto [rid=" + rid + ", isValid=" + isValid + ", requestType=" + requestType + "]";
	}
	
	
	
	

}
