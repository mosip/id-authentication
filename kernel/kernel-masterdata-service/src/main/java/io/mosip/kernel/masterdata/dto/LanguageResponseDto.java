package io.mosip.kernel.masterdata.dto;

import java.util.List;

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
public class LanguageResponseDto {
	private List<LanguageDto> languages;
}
