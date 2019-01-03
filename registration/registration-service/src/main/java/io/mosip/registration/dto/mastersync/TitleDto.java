package io.mosip.registration.dto.mastersync;

/**
 * DTO class for fetching titles from masterdata
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */

public class TitleDto {
	
	private String titleCode;
	private String titleName;
	private String titleDescription;
	private String langCode;
	/**
	 * @return the titleCode
	 */
	public String getTitleCode() {
		return titleCode;
	}
	/**
	 * @param titleCode the titleCode to set
	 */
	public void setTitleCode(String titleCode) {
		this.titleCode = titleCode;
	}
	/**
	 * @return the titleName
	 */
	public String getTitleName() {
		return titleName;
	}
	/**
	 * @param titleName the titleName to set
	 */
	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}
	/**
	 * @return the titleDescription
	 */
	public String getTitleDescription() {
		return titleDescription;
	}
	/**
	 * @param titleDescription the titleDescription to set
	 */
	public void setTitleDescription(String titleDescription) {
		this.titleDescription = titleDescription;
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
