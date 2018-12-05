package io.mosip.preregistration.application.dto;

import java.io.Serializable;

/**
 * Exception json Info
 * 
 * @author M1037717
 *
 */
public class ExceptionJSONInfoDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3999014525078508265L;

	private String errorCode;
	private String message;
	private String causedBy;

	public ExceptionJSONInfoDTO(String errorCode, String message) {
		super();
		this.errorCode = errorCode;
		this.message = message;
	}

	public ExceptionJSONInfoDTO(String errorCode, String message, String causedBy) {
		super();
		this.errorCode = errorCode;
		this.message = message;
		this.causedBy = causedBy;
	}

	public String getErrorcode() {
		return errorCode;
	}

	public String getMessage() {
		return message;
	}

	public String getCausedBy() {
		return causedBy;
	}
}
