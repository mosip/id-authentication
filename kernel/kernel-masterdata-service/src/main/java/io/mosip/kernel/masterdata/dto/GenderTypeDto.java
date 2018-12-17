package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data


public class GenderTypeDto {
	
	@NotNull
	@Size(min = 1, max = 16)
	private String code;

	@NotNull
	@Size(min = 1, max = 64)
	private String genderName;

	@NotNull
	@Size(min = 1, max = 3)
	private String langCode;

	@NotNull
	private Boolean isActive;

}
