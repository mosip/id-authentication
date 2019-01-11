package io.mosip.registration.dto.mastersync;

/**
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */

public class ValidDocumentDto extends MasterSyncBaseDto{

	
	private String docTypeCode;

	
	private String docCategoryCode;

	
	private String langCode;

	
	private Boolean isActive;


	/**
	 * @return the docTypeCode
	 */
	public String getDocTypeCode() {
		return docTypeCode;
	}


	/**
	 * @param docTypeCode the docTypeCode to set
	 */
	public void setDocTypeCode(String docTypeCode) {
		this.docTypeCode = docTypeCode;
	}


	/**
	 * @return the docCategoryCode
	 */
	public String getDocCategoryCode() {
		return docCategoryCode;
	}


	/**
	 * @param docCategoryCode the docCategoryCode to set
	 */
	public void setDocCategoryCode(String docCategoryCode) {
		this.docCategoryCode = docCategoryCode;
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
