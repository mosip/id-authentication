package io.mosip.preregistration.transliteration.errorcode;

public enum ErrorCodes {
	
	PRG_TRL_APP_001("PRG_TRL_APP_001"),
	PRG_TRL_APP_002("PRG_TRL_APP_002"),
	PRG_TRL_APP_003("PRG_TRL_APP_003"),
	PRG_TRL_APP_004("PRG_TRL_APP_004"),
    PRG_TRL_APP_005("PRG_TRL_APP_005"),
	PRG_TRL_APP_006("PRG_TRL_APP_006"),
	PRG_TRL_APP_007("PRG_TRL_APP_007");

	/**
	 * @param code
	 */
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
