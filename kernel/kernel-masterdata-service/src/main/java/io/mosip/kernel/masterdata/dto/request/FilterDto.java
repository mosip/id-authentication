package io.mosip.kernel.masterdata.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.mosip.kernel.masterdata.validator.FilterColumn;
import io.mosip.kernel.masterdata.validator.FilterColumnEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDto {

	@NotBlank
	private String columnName;

	@NotNull
	@FilterColumn(columns = { FilterColumnEnum.ALL, FilterColumnEnum.UNIQUE })
	private String type;
}
