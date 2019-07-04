package io.mosip.preregistration.login.errorcodes;

/**
 * This enum provides different error codes 
 * 
 * @author Akshay
 * @since 1.0.0.
 */
public enum ErrorCodes {

	PRG_AUTH_001("PRG_PAM_LGN_001"),
	PRG_AUTH_002("PRG_PAM_LGN_002"),
	PRG_AUTH_003("PRG_PAM_LGN_003"),
	PRG_AUTH_004("PRG_PAM_LGN_004"),
	PRG_AUTH_005("PRG_PAM_LGN_005"),
	PRG_AUTH_006("PRG_PAM_LGN_006"),
	PRG_AUTH_007("PRG_PAM_LGN_007"),
	PRG_AUTH_008("PRG_PAM_LGN_008"),
	PRG_AUTH_009("PRG_PAM_LGN_009"),
	PRG_AUTH_010("PRG_PAM_LGN_010"),
	PRG_AUTH_011("PRG_PAM_LGN_011"),
	PRG_AUTH_012("PRG_PAM_LGN_012"),
	PRG_AUTH_013("PRG_PAM_LGN_013"),
	PRG_AUTH_014("PRG_PAM_LGN_014");
	
	
	
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
