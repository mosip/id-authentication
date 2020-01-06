package io.mosip.registration.dto.mastersync;

/**
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public class BiometricAttributeDto extends MasterSyncBaseDto {
	/**
	 * Field for code
	 */

	private String code;
	/**
	 * Field for name
	 */

	private String name;

	/**
	 * Field for description
	 */

	private String description;
	/**
	 * Field for biometricTypecode
	 */

	private String biometricTypeCode;
	/**
	 * Field for language code
	 */

	private String langCode;
	/**
	 * Field for the status of data.
	 */

	private Boolean isActive;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	

}
