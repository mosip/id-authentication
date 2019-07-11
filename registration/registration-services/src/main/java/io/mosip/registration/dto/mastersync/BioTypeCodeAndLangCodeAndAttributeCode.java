package io.mosip.registration.dto.mastersync;

/**
 * Response class for Biometric attribute save
 * 
 * @author Sreekar Chukka
 * @version 1.0.0
 */

public class BioTypeCodeAndLangCodeAndAttributeCode extends MasterSyncBaseDto{
	private String code;
	private String biometricTypeCode;
	private String langCode;
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the biometricTypeCode
	 */
	public String getBiometricTypeCode() {
		return biometricTypeCode;
	}
	/**
	 * @param biometricTypeCode the biometricTypeCode to set
	 */
	public void setBiometricTypeCode(String biometricTypeCode) {
		this.biometricTypeCode = biometricTypeCode;
	}
	/**
	 * @return the langCode
	 */
	public String getLangCode() {
		return langCode;
	}
	/**
	 * @param langCode the langCode to set
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	
	
}
