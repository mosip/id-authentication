package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * Dto class for Language.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Data
public class LanguageDto {

	/**
	 * Field for language code
	 */
	@NotNull
	@NotEmpty
	private String languageCode;

	/**
	 * Field for language name
	 */
	private String languageName;

	/**
	 * Field for language family
	 */
	private String languageFamily;

	/**
	 * Field for language native name
	 */
	private String nativeName;

	/**
	 * Field for the status of data.
	 */
	private Boolean isActive;

}
