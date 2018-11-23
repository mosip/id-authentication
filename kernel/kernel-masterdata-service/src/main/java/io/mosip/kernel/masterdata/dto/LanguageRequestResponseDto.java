package io.mosip.kernel.masterdata.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class work as a request and response object which will be provided by
 * the user or given by the MOSIP system respectively.
 * 
 * @author Bal Vikash Sharma
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LanguageRequestResponseDto {

	/**
	 * List of Languages used for Create or Updated for languages.
	 */
	private List<LanguageDto> languages;

	/**
	 * List of successfully language created.
	 */
	private List<String> successfullyCreatedLanguages;

}
