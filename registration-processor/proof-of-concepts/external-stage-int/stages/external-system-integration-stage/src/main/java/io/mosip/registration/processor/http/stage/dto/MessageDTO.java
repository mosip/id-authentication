package io.mosip.registration.processor.http.stage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageDTO {

	private String rid;
	private boolean isValid;
	private boolean internalError;

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

	public void setIsValid(boolean isValid) {
		this.isValid = isValid;
	}

	@JsonProperty(value = "internalError")
	public boolean internalError() {
		return internalError;
	}

	public void setInternalError(boolean internalError) {
		this.internalError = internalError;
	}

	@Override
	public String toString() {
		return "MessageDTO [rid=" + rid + ", isValid=" + isValid + ", internalError=" + internalError + "]";
	}

}
