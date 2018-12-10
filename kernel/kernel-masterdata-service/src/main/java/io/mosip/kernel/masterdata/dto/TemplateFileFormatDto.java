package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data


public class TemplateFileFormatDto {
	private String code;
	private String description;
	private String langCode;
	private Boolean isActive;
}
