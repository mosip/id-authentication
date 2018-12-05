package io.mosip.kernel.synchandler.dto;

import lombok.Data;

@Data
public class DocumentTypeRequestDto {
	private String id;
	private String ver;
	private String timestamp;
	private DocumentTypeListDto request;
}
