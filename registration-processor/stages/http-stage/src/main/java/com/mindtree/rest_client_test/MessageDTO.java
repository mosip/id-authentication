package com.mindtree.rest_client_test;

public class MessageDTO {
	
	private String rid;
	private boolean isValid;
	private boolean isInternalError;
	
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public boolean isInternalError() {
		return isInternalError;
	}
	public void setInternalError(boolean isInternalError) {
		this.isInternalError = isInternalError;
	}
	
	

}
