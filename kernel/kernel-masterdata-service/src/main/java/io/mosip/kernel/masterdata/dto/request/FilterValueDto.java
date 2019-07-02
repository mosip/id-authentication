package io.mosip.kernel.masterdata.dto.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
	private List<FilterDto> filters;
	@NotBlank
	private String LanguageCode;
}
