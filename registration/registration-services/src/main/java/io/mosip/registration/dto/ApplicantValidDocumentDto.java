package io.mosip.registration.dto;

/**
 * The DTO Class ApplicantValidDocumentDto.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */
public class ApplicantValidDocumentDto {

	private String appTypeCode;

	private String docTypeCode;

	private String docCatCode;

	private String langCode;

	private Boolean isDeleted;

	private Boolean isActive;

	/**
	 * @return the isDeleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted the isDeleted to set
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
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
	 * @return the appTypeCode
	 */
	public String getAppTypeCode() {
		return appTypeCode;
	}

	/**
	 * @param appTypeCode the appTypeCode to set
	 */
	public void setAppTypeCode(String appTypeCode) {
		this.appTypeCode = appTypeCode;
	}

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
	 * @return the docCatCode
	 */
	public String getDocCatCode() {
		return docCatCode;
	}

	/**
	 * @param docCatCode the docCatCode to set
	 */
	public void setDocCatCode(String docCatCode) {
		this.docCatCode = docCatCode;
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
