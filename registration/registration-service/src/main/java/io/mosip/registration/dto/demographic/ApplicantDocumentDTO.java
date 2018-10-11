package io.mosip.registration.dto.demographic;

import java.util.List;

import io.mosip.registration.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class used to capture the documents, photograph, exceptional photograph
 * and Acknowledgement Receipt of the Individual
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicantDocumentDTO extends BaseDTO {
	
	private List<DocumentDetailsDTO> documentDetailsDTO;
	private String photographName;
	private byte[] photo;
	private boolean hasExceptionPhoto;
	private byte[] exceptionPhoto;
	private String exceptionPhotoName;
	private double qualityScore;
	private int numRetry;
	private byte[] acknowledgeReceipt;
	private String acknowledgeReceiptName;
}
