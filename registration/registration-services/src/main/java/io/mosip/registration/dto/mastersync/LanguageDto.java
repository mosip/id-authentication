package io.mosip.registration.dto.mastersync;

/**
 * Data transfer object class for Language.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */

public class LanguageDto extends MasterSyncBaseDto{

	/**
	 * Field for language code
	 */
	
	private String code;

	/**
	 * Field for language name
	 */
	
	private String name;

	/**
	 * Field for language family
	 */
	
	private String family;

	/**
	 * Field for language native name
	 */
	
	private String nativeName;

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
	 * @return the family
	 */
	public String getFamily() {
		return family;
	}

	/**
	 * @param family the family to set
	 */
	public void setFamily(String family) {
		this.family = family;
	}

	/**
	 * @return the nativeName
	 */
	public String getNativeName() {
		return nativeName;
	}

	/**
	 * @param nativeName the nativeName to set
	 */
	public void setNativeName(String nativeName) {
		this.nativeName = nativeName;
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

//	@NotNull
//	private LocalTime lunchStartTime;
	
//	@NotNull
//	private LocalDateTime createdDateTime;
	
	

}
