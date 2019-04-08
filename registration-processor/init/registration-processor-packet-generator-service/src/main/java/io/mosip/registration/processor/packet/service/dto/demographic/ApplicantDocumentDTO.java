package io.mosip.registration.processor.packet.service.dto.demographic;

import java.io.Serializable;
import java.util.Map;

import io.mosip.registration.processor.packet.service.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class used to capture the documents, photograph, exceptional photograph
 * and Acknowledgement Receipt of the Individual.
 *
 * @author Sowmya
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicantDocumentDTO extends BaseDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 6140664025425342026L;

	/** The documents. */
	private Map<String, DocumentDetailsDTO> documents;

	/** The photograph name. */
	private String photographName;

	/** The photo. */
	private byte[] photo;

	/** The compressed photo for QR Code. */
	private byte[] compressedFacePhoto;

	/** The has exception photo. */
	private boolean hasExceptionPhoto;

	/** The exception photo. */
	private byte[] exceptionPhoto;

	/** The exception photo name. */
	private String exceptionPhotoName;

	/** The quality score. */
	private double qualityScore;

	/** The num retry. */
	private int numRetry;

	/** The acknowledge receipt. */
	private byte[] acknowledgeReceipt;

	/** The acknowledge receipt name. */
	private String acknowledgeReceiptName;

}
