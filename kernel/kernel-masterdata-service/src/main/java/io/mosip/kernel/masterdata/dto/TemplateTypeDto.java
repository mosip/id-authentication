package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateTypeDto {

	private String code;
	private String langCode;
	private String description;
	private Boolean isActive;

}
