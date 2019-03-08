package io.mosip.registration.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Embeddable
public class ValidDocumentId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6394443536056430885L;

	@Column(name = "doctyp_code")
	private String docTypeCode;

	@Column(name = "doccat_code")
	private String docCatCode;

	@Column(name = "apptyp_code")
	private String appTypeCode;

	public String getDocTypeCode() {
		return docTypeCode;
	}

	public void setDocTypeCode(String docTypeCode) {
		this.docTypeCode = docTypeCode;
	}

	public String getDocCatCode() {
		return docCatCode;
	}

	public void setDocCatCode(String docCatCode) {
		this.docCatCode = docCatCode;
	}

	public String getAppTypeCode() {
		return appTypeCode;
	}

	public void setAppTypeCode(String appTypeCode) {
		this.appTypeCode = appTypeCode;
	}

}
