package io.mosip.registration.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import io.mosip.registration.entity.ApplicantValidDocument;
import lombok.Data;

/**
 * Composite key for {@link ApplicantValidDocument}
 *
 * @author Sreekar Chukka
 */
@Embeddable
@Data
public class ApplicantValidDocumentID implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "apptyp_code")
	private String appTypeCode;

	@Column(name = "doccat_code")
	private String docCatCode;

	@Column(name = "doctyp_code")
	private String docTypeCode;

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

}
