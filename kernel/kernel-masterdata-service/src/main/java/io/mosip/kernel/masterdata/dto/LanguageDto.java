package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object class for Language.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguageDto {

	/**
	 * Field for language code
	 */
	private String code;

	/**
	 * Field for language name
	 */
	private String name;

	/**
	 * Field for language family
	 */
	private String family;

	/**
	 * Field for language native name
	 */
	private String nativeName;

	/**
	 * Field for the status of data.
	 */
	private Boolean isActive;

}
