package org.mosip.registration.processor.dto.json.metadata;

import lombok.Data;

@Data
public class DocumentDetails {
	private String documentCategory;
	private String documentOwner;
	private String documentType;
	private String documentName;

}
