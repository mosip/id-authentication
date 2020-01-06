package io.mosip.registration.dto.mastersync;

/**
 * Class that holds the variables of each registration center type list data to
 * be added.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */

public class RegistrationCenterTypeDto extends MasterSyncBaseDto{
	/**
	 * the code.
	 */
	private String code;
	/**
	 * the language code.
	 */
	private String langCode;
	/**
	 * the name.
	 */
	private String name;
	/**
	 * the description.
	 */
	private String descr;
	
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
	 * @return the descr
	 */
	public String getDescr() {
		return descr;
	}

	/**
	 * @param descr the descr to set
	 */
	public void setDescr(String descr) {
		this.descr = descr;
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
