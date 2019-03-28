package io.mosip.registration.dto.mastersync;

/**
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */

public class ApplicantValidDocumentDto extends MasterSyncBaseDto {

	private String appTypeCode;

	private String docTypeCode;

	private String docCatCode;

	private String langCode;

	private Boolean isActive;

	public String getAppTypeCode() {
		return appTypeCode;
	}

	public void setAppTypeCode(String appTypeCode) {
		this.appTypeCode = appTypeCode;
	}

	public String getDocCatCode() {
		return docCatCode;
	}

	public void setDocCatCode(String docCatCode) {
		this.docCatCode = docCatCode;
	}

	/**
	 * @return the docTypeCode
	 */
	public String getDocTypeCode() {
		return docTypeCode;
	}

	/**
	 * @param docTypeCode
	 *            the docTypeCode to set
	 */
	public void setDocTypeCode(String docTypeCode) {
		this.docTypeCode = docTypeCode;
	}

	/**
	 * @return the langCode
	 */
	public String getLangCode() {
		return langCode;
	}

	/**
	 * @param langCode
	 *            the langCode to set
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
	 * @param isActive
	 *            the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

}
