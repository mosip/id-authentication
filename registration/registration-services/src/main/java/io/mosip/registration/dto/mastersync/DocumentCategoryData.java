package io.mosip.registration.dto.mastersync;

/**
 * Response dto for Document Category Detail
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */

public class DocumentCategoryData extends MasterSyncBaseDto{

	/**
	 * Document category code.
	 */
	
	private String code;

	/**
	 * Document category name.
	 */
	private String name;

	/**
	 * Document category description
	 */
	private String description;

	/**
	 * The Language Code.
	 */
	private String langCode;

	/**
	 * Is active or not.
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
