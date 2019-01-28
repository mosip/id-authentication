package io.mosip.registration.processor.core.packet.dto;

/**
 * This class contains the attributes to be displayed for Document object in
 * PacketMetaInfo JSON.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class Document {

	/** The document name. */
	private String documentName;

	/** The document category. */
	private String documentCategory;

	/** The document owner. */
	private String documentOwner;

	/** The document type. */
	private String documentType;

	private String format;

	/**
	 * Gets the document name.
	 *
	 * @return the documentName
	 */
	public String getDocumentName() {
		return documentName;
	}

	/**
	 * Sets the document name.
	 *
	 * @param documentName
	 *            the documentName to set
	 */
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	/**
	 * Gets the document category.
	 *
	 * @return the documentCategory
	 */
	public String getDocumentCategory() {
		return documentCategory;
	}

	/**
	 * Sets the document category.
	 *
	 * @param documentCategory
	 *            the documentCategory to set
	 */
	public void setDocumentCategory(String documentCategory) {
		this.documentCategory = documentCategory;
	}

	/**
	 * Gets the document owner.
	 *
	 * @return the documentOwner
	 */
	public String getDocumentOwner() {
		return documentOwner;
	}

	/**
	 * Sets the document owner.
	 *
	 * @param documentOwner
	 *            the documentOwner to set
	 */
	public void setDocumentOwner(String documentOwner) {
		this.documentOwner = documentOwner;
	}

	/**
	 * Gets the document type.
	 *
	 * @return the documentType
	 */
	public String getDocumentType() {
		return documentType;
	}

	/**
	 * Sets the document type.
	 *
	 * @param documentType
	 *            the documentType to set
	 */
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
