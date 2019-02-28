package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class IndividualTypeDto {

	@NotBlank
	private String code;
	@NotBlank
	private String langCode;
	@NotBlank
	private String name;
	@NotNull
	private Boolean isActive;

}
