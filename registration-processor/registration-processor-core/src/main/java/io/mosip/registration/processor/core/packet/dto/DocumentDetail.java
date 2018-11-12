package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

/**
 * Instantiates a new document detail.
 */
@Data
public class DocumentDetail {

	/** The document category. */
	private String documentCategory;

	/** The document owner. */
	private String documentOwner;

	/** The document type. */
	private String documentType;

	/** The document name. */
	private String documentName;

}
