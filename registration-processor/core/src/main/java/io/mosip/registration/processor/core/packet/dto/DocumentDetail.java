package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class DocumentDetail {
	private String documentCategory;
	private String documentOwner;
	private String documentType;
	private String documentName;

}
