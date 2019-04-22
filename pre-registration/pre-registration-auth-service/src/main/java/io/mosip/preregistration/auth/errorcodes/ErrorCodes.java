package io.mosip.preregistration.auth.errorcodes;

/**
 * This enum provides different error codes 
 * 
 * @author Akshay
 * @since 1.0.0.
 */
public enum ErrorCodes {

	PRG_AUTH_001("PRG_AUTH_001"),
	PRG_AUTH_002("PRG_AUTH_002"),
	PRG_AUTH_003("PRG_AUTH_003"),
	PRG_AUTH_004("PRG_AUTH_004"),
	PRG_AUTH_005("PRG_AUTH_005"),
	PRG_AUTH_006("PRG_AUTH_006"),
	PRG_AUTH_007("PRG_AUTH_007"),
	PRG_AUTH_008("PRG_AUTH_008"),
	PRG_AUTH_009("PRG_AUTH_009"),
	PRG_AUTH_010("PRG_AUTH_010"),
	PRG_AUTH_011("PRG_AUTH_011"),
	PRG_AUTH_012("PRG_AUTH_012");
	
	
	
	private ErrorCodes(String code) {
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
