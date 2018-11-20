package io.mosip.registration.dto.json.metadata;

/**
 * This class contains the attributes to be displayed for Document object in
 * PacketMetaInfo JSON
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class Document {

	private String documentName;
	private String documentCategory;
	private String documentOwner;
	private String documentType;

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
		this.documentName = documentName;
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
