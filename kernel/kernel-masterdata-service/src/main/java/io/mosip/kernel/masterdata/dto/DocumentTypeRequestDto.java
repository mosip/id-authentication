package io.mosip.kernel.masterdata.dto;

import lombok.Data;

@Data
public class DocumentTypeRequestDto {
	private String id;
	private String ver;
	private String timestamp;
	private DocumentTypeListDto request;
}
