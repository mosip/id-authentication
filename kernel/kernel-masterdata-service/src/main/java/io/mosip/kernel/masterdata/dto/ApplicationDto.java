package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDto {

	private String code;

	private String name;

	private String description;

	private String langCode;
	
	private Boolean isActive;

}
