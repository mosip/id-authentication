package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the applicant_document database table.
 * 
 * @author Horteppa M1048399
 *
 */
@Embeddable
public class ApplicantDocumentPKEntity implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="reg_id")
	private String regId;

	@Column(name="doc_cat_code")
	private String docCatCode;

	@Column(name="doc_typ_code")
	private String docTypCode;

	

	public ApplicantDocumentPKEntity() {
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

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ApplicantDocumentPKEntity)) {
			return false;
		}
		ApplicantDocumentPKEntity castOther = (ApplicantDocumentPKEntity)other;
		return 
			this.regId.equals(castOther.regId)
			&& this.docCatCode.equals(castOther.docCatCode);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.regId.hashCode();
		hash = hash * prime + this.docCatCode.hashCode();
		hash = hash * prime + this.docTypCode.hashCode();
		
		return hash;
	}
}