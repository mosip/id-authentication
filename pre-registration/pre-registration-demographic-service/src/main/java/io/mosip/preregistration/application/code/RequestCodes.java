/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.code;

/**
 * 
 * This Enum provides the constant variables to accept input request.
 * 
 * @author Ravi C Balaji
 * @since 1.0.0
 *
 */
public enum RequestCodes {

	/* id */
	ID("id"),

	/* version */
	VER("ver"),

	/* request date time */
	REQ_TIME("reqTime"),

	/* request object */
	REQUEST("request"),

	/* user id */
	USER_ID("userId"),

	/* preRegistration Id */
	PRE_REGISTRAION_ID("preRegistrationId"),

	/* create by */
	CREATED_BY("createdBy"),

	/* created Date time */
	CREATED_DATE_TIME("createdDatetime"),

	/* updated By */
	UPDATED_BY("updatedBy"),

	/* updated Date time */
	UPDATED_DATE_TIME("updatedDatetime"),

	/* status Code */
	STATUS_CODE("statusCode"),

	/* language Code */
	LANG_CODE("langCode"),

	/* demographic json Details */
	DEMOGRAPHIC_DETATILS("demographicDetails"),

	/* identity details */
	IDENTITY("identity"),

	/* value */
	VALUE("value"),

	/* language */
	LANGUAGE("language"),

	/* label */
	LABEL("label"),

	/* Full name */
	FULLNAME("fullName"),

	/* date of birth */
	DOB("dateOfBirth"),

	/* gender */
	GENDER("gender"),

	/* from date */
	FROM_DATE("fromDate"),

	/* to date */
	TO_DATE("toDate"),
	
	/* save*/
	SAVE("save"),
	
	/* update */
	UPDATE("update");
	
	/**
	 * @param code
	 */
	private RequestCodes(String code) {
		this.code = code;
	}

	/**
	 * Code
	 */
	private final String code;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
}
