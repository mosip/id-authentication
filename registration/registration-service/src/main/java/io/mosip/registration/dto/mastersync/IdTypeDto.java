package io.mosip.registration.dto.mastersync;

/**
 * DTO class for IdType.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */

public class IdTypeDto {
	/**
	 * the id code.
	 */
	private String code;
	/**
	 * the id description.
	 */
	private String description;
	/**
	 * the language code.
	 */
	private String langCode;

	private String name;

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

}
