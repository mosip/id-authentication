package io.mosip.registration.dto.demographic;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.mosip.registration.dto.BaseDTO;

/**
 * This class used to capture the Documents' details of the Individual
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
public class DocumentDetailsDTO extends BaseDTO {
	
	@JsonIgnore
	private byte[] document;
	protected String documentName;
	protected String documentCategory;
	protected String documentOwner;
	protected String documentType;

	/**
	 * @return the document
	 */
	public byte[] getDocument() {
		return document;
	}

	/**
	 * @param document
	 *            the document to set
	 */
	public void setDocument(byte[] document) {
		this.document = document;
	}

	/**
	 * @return the documentName
	 */
	public String getDocumentName() {
		return documentName;
	}

	/**
	 * @param documentName
	 *            the documentName to set
	 */
	public void setDocumentName(String documentName) {
		this.documentName = documentCategory.concat("_").concat(documentName).concat(".").concat(documentType);
	}

	/**
	 * @return the documentCategory
	 */
	public String getDocumentCategory() {
		return documentCategory;
	}

	/**
	 * @param documentCategory
	 *            the documentCategory to set
	 */
	public void setDocumentCategory(String documentCategory) {
		this.documentCategory = documentCategory;
	}

	/**
	 * @return the documentOwner
	 */
	public String getDocumentOwner() {
		return documentOwner;
	}

	/**
	 * @param documentOwner
	 *            the documentOwner to set
	 */
	public void setDocumentOwner(String documentOwner) {
		this.documentOwner = documentOwner;
	}

	/**
	 * @return the documentType
	 */
	public String getDocumentType() {
		return documentType;
	}

	/**
	 * @param documentType
	 *            the documentType to set
	 */
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

}
