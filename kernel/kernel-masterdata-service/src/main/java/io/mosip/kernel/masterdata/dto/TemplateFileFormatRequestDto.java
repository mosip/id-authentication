package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateFileFormatRequestDto {

	private String id;
	private String ver;
	private String timestamp;
	private TemplateFileFormatData request;
	
}
