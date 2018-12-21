package io.mosip.registration.dto.mastersync;

import java.util.ArrayList;
import java.util.List;

public class ReasonCategoryDto {

	private String code;

	private String name;

	private String description;

	private String langCode;

	private List<ReasonListDto> reasonLists = new ArrayList<ReasonListDto>();

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
	 * @return the reasonLists
	 */
	public List<ReasonListDto> getReasonLists() {
		return reasonLists;
	}

	/**
	 * @param reasonLists the reasonLists to set
	 */
	public void setReasonLists(List<ReasonListDto> reasonLists) {
		this.reasonLists = reasonLists;
	}

	

}
