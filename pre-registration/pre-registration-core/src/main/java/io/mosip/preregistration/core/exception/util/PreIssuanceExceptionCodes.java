package io.mosip.preregistration.core.exception.util;

public final class PreIssuanceExceptionCodes {
	
	private static final String PRE_ID_LOGIN_PREFIX = "REG-AUTH-LOGIN";
	private static final String PRE_ID_UPDATE_PREFIX = "REG-AUTH-UPDATE";
	private static final String PRE_ID_REGISTARTION_PREFIX = "REG-AUTH-REGISTRATION";
	

	private PreIssuanceExceptionCodes() {
		throw new IllegalStateException("Utility class");
	}
	
	
	public static final String INVALID_USER_NAME = PRE_ID_LOGIN_PREFIX+"001-";
	public static final String USER_INSERTION = PRE_ID_LOGIN_PREFIX+"002-";
	public static final String USER_ALREADY_EXIST = PRE_ID_UPDATE_PREFIX+"003-";
	public static final String TABLE_NOT_FOUND_EXCEPTION = PRE_ID_REGISTARTION_PREFIX+"004-";
	
}
