/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.datasync.code;

/**
 * 
 * This Enum provides the constant variables to accept input request.
 * 
 * @author Ravi C Balaji
 * @since 1.0.0
 *
 */
public enum RequestCodes {
	ID("id"), 
	VER("ver"), 
	REQ_TIME("reqTime"), 
	REQUEST("request"), 
	PRE_REGISTARTION_ID("preRegistrationId"), 
	CREATED_BY("createdBy"), 
	CREATED_DATE_TIME("createdDatetime"), 
	UPDATED_BY("updatedBy"), 
	UPDATED_DATE_TIME("updatedDatetime"), 
	STATUS_CODE("statusCode"), 
	LANG_CODE("langCode"), 
	REGISTARTION_CLIENT_ID("regClientId"), 
	FROM_DATE("fromDate"), 
	TO_DATE("toDate"), 
	USER_ID("userId");
	
	/**
	 * @param code
	 */
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
