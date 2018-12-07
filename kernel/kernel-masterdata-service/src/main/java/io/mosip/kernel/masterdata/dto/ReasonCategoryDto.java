package io.mosip.kernel.masterdata.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class ReasonCategoryDto {

	@NotNull
	@Size(min = 1, max = 36)
	private String code;

	@NotNull
	@Size(min = 1, max = 64)
	private String name;

	@Size(min = 1, max = 128)
	private String description;

	@NotNull
	@Size(min = 1, max = 3)
	private String langCode;

	@NotNull
	private Boolean isActive;

	private Boolean isDeleted;

	private List<ReasonListDto> reasonList = new ArrayList<>();
}
