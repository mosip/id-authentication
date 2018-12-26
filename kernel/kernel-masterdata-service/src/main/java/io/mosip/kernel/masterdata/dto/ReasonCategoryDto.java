package io.mosip.kernel.masterdata.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReasonCategoryDto {

	@NotBlank
	@Size(min = 1, max = 36)
	private String code;

	@NotBlank
	@Size(min = 1, max = 64)
	private String name;

	
	@Size(min = 1, max = 128)
	private String description;

	@NotBlank
	@Size(min = 1, max = 3)
	private String langCode;

	@NotBlank
	private Boolean isActive;

	private List<ReasonListDto> reasonList = new ArrayList<>();
}
