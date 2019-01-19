package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the applicant_document database table.
 * 
 * @author Horteppa M1048399
 *
 */
@Embeddable
public class ApplicantDocumentPKEntity implements Serializable {

	/** The Constant serialVersionUID. */
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	/** The reg id. */
	@Column(name = "reg_id")
	private String regId;

	/** The doc cat code. */
	@Column(name = "doc_cat_code", nullable = false)
	private String docCatCode;

	/** The doc typ code. */
	@Column(name = "doc_typ_code", nullable = false)
	private String docTypCode;

	/**
	 * Instantiates a new applicant document PK entity.
	 */
	public ApplicantDocumentPKEntity() {
		super();
	}

	/**
	 * Gets the reg id.
	 *
	 * @return the reg id
	 */
	public String getRegId() {
		return this.regId;
	}

	/**
	 * Sets the reg id.
	 *
	 * @param regId the new reg id
	 */
	public void setRegId(String regId) {
		this.regId = regId;
	}

	/**
	 * Gets the doc cat code.
	 *
	 * @return the doc cat code
	 */
	public String getDocCatCode() {
		return this.docCatCode;
	}

	/**
	 * Sets the doc cat code.
	 *
	 * @param docCatCode the new doc cat code
	 */
	public void setDocCatCode(String docCatCode) {
		this.docCatCode = docCatCode;
	}

	/**
	 * Gets the doc typ code.
	 *
	 * @return the doc typ code
	 */
	public String getDocTypCode() {
		return this.docTypCode;
	}

	/**
	 * Sets the doc typ code.
	 *
	 * @param docTypCode the new doc typ code
	 */
	public void setDocTypCode(String docTypCode) {
		this.docTypCode = docTypCode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ApplicantDocumentPKEntity)) {
			return false;
		}
		ApplicantDocumentPKEntity castOther = (ApplicantDocumentPKEntity) other;
		return this.regId.equals(castOther.regId) && this.docCatCode.equals(castOther.docCatCode);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
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