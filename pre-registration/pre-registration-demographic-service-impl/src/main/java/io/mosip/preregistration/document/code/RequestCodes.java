/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.document.code;

/**
 * 
 * This Enum provides the constant variables to accept input request.
 * 
 * @author Rajath KR
 * @since 1.0.0
 *
 */
public enum RequestCodes {

	/* id */
	ID("id"),

	/* version */
	VERSION("version"),

	/* request date time */
	REQ_TIME("requesttime"),

	/* request object */
	REQUEST("request"),

	/* user id */
	USERID("userId"),

	/* preRegistration Id */
	PREREGISTRATIONID("preRegistrationId"),

	/* create by */
	CREATEDBY("createdBy"),

	/* created Date time */
	CR_DATE_TIME("createdDatetime"),

	/* updated By */
	UPDATEDBY("updatedBy"),

	/* updated Date time */
	UPDATED_DATE_TIME("updatedDatetime"),

	/* status Code */
	STATUSCODE("statusCode"),

	/* language Code */
	LANGCODE("langCode");
	
	private RequestCodes(String code) {
		this.code = code;
	}

	private final String code;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
}
