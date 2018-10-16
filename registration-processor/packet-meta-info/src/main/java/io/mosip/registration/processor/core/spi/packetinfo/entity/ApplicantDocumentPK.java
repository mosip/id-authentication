package io.mosip.registration.processor.core.spi.packetinfo.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the applicant_document database table.
 * 
 */
@Embeddable
public class ApplicantDocumentPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="reg_id")
	private String regId;

	@Column(name="doc_cat_code")
	private String docCatCode;

	@Column(name="doc_typ_code")
	private String docTypCode;

	@Column(name="lang_code")
	private String langCode;

	public ApplicantDocumentPK() {
		super();
	}
	
	public String getRegId() {
		return this.regId;
	}
	public void setRegId(String regId) {
		this.regId = regId;
	}
	public String getDocCatCode() {
		return this.docCatCode;
	}
	public void setDocCatCode(String docCatCode) {
		this.docCatCode = docCatCode;
	}
	public String getDocTypCode() {
		return this.docTypCode;
	}
	public void setDocTypCode(String docTypCode) {
		this.docTypCode = docTypCode;
	}
	public String getLangCode() {
		return this.langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	
}