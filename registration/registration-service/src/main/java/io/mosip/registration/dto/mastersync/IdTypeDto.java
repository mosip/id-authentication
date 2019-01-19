package io.mosip.registration.dto.mastersync;

/**
 * DTO class for IdType fetch response.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */

public class IdTypeDto extends MasterSyncBaseDto{
	/**
	 * The id code.
	 */
	private String code;
	/**
	 * The id description.
	 */
	private String descr;
	
	
	/**
	 * The id name.
	 */
	private String name;
	
	/**
	 * The language code.
	 */
	private String langCode;
	
	private Boolean isActive;
	
	

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

