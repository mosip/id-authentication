package io.mosip.registration.dto.json.metadata;

import java.util.List;

/**
 * This class is to capture the json parsing document data
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
public class Document {
	private List<DocumentDetails> documentDetails;
	private String registrationAckCopy;
	public List<DocumentDetails> getDocumentDetails() {
		return documentDetails;
	}
	public void setDocumentDetails(List<DocumentDetails> documentDetails) {
		this.documentDetails = documentDetails;
	}
	public String getRegistrationAckCopy() {
		return registrationAckCopy;
	}
	public void setRegistrationAckCopy(String registrationAckCopy) {
		this.registrationAckCopy = registrationAckCopy;
	}	
}
		
