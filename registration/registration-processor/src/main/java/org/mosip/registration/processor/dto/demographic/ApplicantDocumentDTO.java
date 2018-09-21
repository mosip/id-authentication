package org.mosip.registration.processor.dto.demographic;

import java.util.List;

import org.mosip.registration.processor.dto.BaseDTO;

import lombok.Data;

/**
 * Applicant document details and its data
 * 
 * @author M1047595
 *
 */
@Data
public class ApplicantDocumentDTO extends BaseDTO {
	private List<DocumentDetailsDTO> documentDetailsDTO;
	private String photoName;
	private byte[] photo;
	private boolean hasExceptionPhoto;
	private byte[] exceptionPhoto;
	private String exceptionPhotoName;
	private byte[] acknowledgeReceipt;
	private String acknowledgeReceiptName;
}
