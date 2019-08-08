package io.mosip.kernel.masterdata.dto.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.mosip.kernel.masterdata.validator.ValidLangCode;
import lombok.Data;

/**
 * Filter request dto
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data
public class FilterValueDto {
	@NotNull
	@Valid
	private List<FilterDto> filters;
	// @NotBlank
	@ValidLangCode
	private String languageCode;
}
