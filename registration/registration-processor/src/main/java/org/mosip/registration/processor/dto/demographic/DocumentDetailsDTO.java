package org.mosip.registration.processor.dto.demographic;

import lombok.Data;

/**
 * Applicant Document details
 * @author M1047595
 *
 */
@Data
public class DocumentDetailsDTO {
	private byte[] document;
	private String documentName;
	private String documentCategory;
	private String documentOwner;
	private String documentType;

}
