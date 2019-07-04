package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.mosip.registration.entity.id.ApplicantValidDocumentID;

/**
 * Entity class for valid document.
 * 
 * @author Sreekar chukka
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "applicant_valid_document", schema = "reg")
public class ApplicantValidDocument extends RegistrationCommonFields implements Serializable {

	private static final long serialVersionUID = -3111581667845281498L;

	@EmbeddedId
	private ApplicantValidDocumentID validDocument;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "doccat_code", referencedColumnName = "code", insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false), })
	DocumentCategory documentCategory;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "doctyp_code", referencedColumnName = "code", insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false) })
	DocumentType documentType;

	

	public ApplicantValidDocumentID getValidDocument() {
		return validDocument;
	}

	public void setValidDocument(ApplicantValidDocumentID validDocument) {
		this.validDocument = validDocument;
	}

	@Column(name = "lang_code")
	private String langCode;

	/**
	 * @return the documentCategory
	 */
	public DocumentCategory getDocumentCategory() {
		return documentCategory;
	}

	/**
	 * @param documentCategory the documentCategory to set
	 */
	public void setDocumentCategory(DocumentCategory documentCategory) {
		this.documentCategory = documentCategory;
	}

	/**
	 * @return the documentType
	 */
	public DocumentType getDocumentType() {
		return documentType;
	}

	/**
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
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
