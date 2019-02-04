package io.mosip.registration.dto.mastersync;

/**
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public class MasterReasonListDto extends MasterSyncBaseDto {

	private String code;

	private String name;

	private String description;

	private String rsnCatCode;

	private String langCode;

	private Boolean isActive;

	private boolean isDeleted;

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
	 * @return the rsnCatCode
	 */
	public String getRsnCatCode() {
		return rsnCatCode;
	}

	/**
	 * @param rsnCatCode the rsnCatCode to set
	 */
	public void setRsnCatCode(String rsnCatCode) {
		this.rsnCatCode = rsnCatCode;
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

	/**
	 * @return the isDeleted
	 */
	public boolean isDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted the isDeleted to set
	 */
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

}
