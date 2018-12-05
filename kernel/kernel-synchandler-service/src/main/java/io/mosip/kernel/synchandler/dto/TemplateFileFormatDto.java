package io.mosip.kernel.synchandler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateFileFormatDto {
	private String code;
	private String description;
	private String langCode;
	private Boolean isActive;
}
