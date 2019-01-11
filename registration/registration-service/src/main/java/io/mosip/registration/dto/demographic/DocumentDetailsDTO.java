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
	protected String value;
	protected String type;
	@JsonIgnore
	protected String owner;
	protected String format;

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
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the name of the document to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return type;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category) {
		this.type = category;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            the file type of the document to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

}
