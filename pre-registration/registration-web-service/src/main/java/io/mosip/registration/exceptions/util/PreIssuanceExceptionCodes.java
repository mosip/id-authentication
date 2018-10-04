package io.mosip.registration.exceptions.util;

public final class PreIssuanceExceptionCodes {
	
	private static final String PRE_ID_LOGIN_PREFIX = "REG-AUTH-LOGIN";
	private static final String PRE_ID_UPDATE_PREFIX = "REG-AUTH-UPDATE";
	

	private PreIssuanceExceptionCodes() {
		throw new IllegalStateException("Utility class");
	}
	
	
	public static final String INVALID_USER_NAME = PRE_ID_LOGIN_PREFIX+"001-";
	public static final String USER_INSERTION = PRE_ID_LOGIN_PREFIX+"002-";
	public static final String USER_ALREADY_EXIST = PRE_ID_UPDATE_PREFIX+"003-";
	
}
