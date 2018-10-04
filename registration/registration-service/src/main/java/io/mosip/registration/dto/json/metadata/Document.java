package io.mosip.registration.dto.json.metadata;

import java.util.List;

public class Document {
	private List<DocumentDetails> documentDetails;
	private String registrationAckCopy;

	/**
	 * @return the documentDetails
	 */
	public List<DocumentDetails> getDocumentDetails() {
		return documentDetails;
	}

	/**
	 * @param documentDetails
	 *            the documentDetails to set
	 */
	public void setDocumentDetails(List<DocumentDetails> documentDetails) {
		this.documentDetails = documentDetails;
	}

	/**
	 * @return the registrationAckCopy
	 */
	public String getRegistrationAckCopy() {
		return registrationAckCopy;
	}

	/**
	 * @param registrationAckCopy
	 *            the registrationAckCopy to set
	 */
	public void setRegistrationAckCopy(String registrationAckCopy) {
		this.registrationAckCopy = registrationAckCopy;
	}
}
		
