package io.mosip.kernel.masterdata.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Bal Vikash Sharma
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LanguageRequestResponseDto {

	@NotNull
	private List<LanguageDto> languages;
}
