package io.mosip.kernel.masterdata.dto;

import lombok.Data;

@Data
public class DocumentCategoryRequestDto {
	private String id;
	private String ver;
	private String timestamp;
	private DocumentCategoryListDto request;
}
