package io.mosip.registration.processor.core.packet.dto;

import java.util.List;

import lombok.Data;

/**
 * Instantiates a new document.
 */
@Data
public class Document {

	/** The document details. */
	private List<DocumentDetail> documentDetails;

	/** The registration ack copy. */
	private String registrationAckCopy;
}
