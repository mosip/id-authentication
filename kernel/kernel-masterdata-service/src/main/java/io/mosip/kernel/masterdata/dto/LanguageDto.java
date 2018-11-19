package io.mosip.kernel.masterdata.dto;

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

}
