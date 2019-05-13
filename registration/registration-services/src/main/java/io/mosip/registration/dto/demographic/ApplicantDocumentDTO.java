package io.mosip.registration.dto.demographic;

import java.util.Map;

import io.mosip.registration.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * This class used to capture the documents, photograph, exceptional photograph
 * and Acknowledgement Receipt of the Individual.
 *
 * @author Dinesh Asokan
 * @since 1.0.0
 */
@Getter
@Setter
public class ApplicantDocumentDTO extends BaseDTO {

	/** The documents. */
	private Map<String, DocumentDetailsDTO> documents;

	/** The acknowledge receipt. */
	private byte[] acknowledgeReceipt;

	/** The acknowledge receipt name. */
	private String acknowledgeReceiptName;

}
