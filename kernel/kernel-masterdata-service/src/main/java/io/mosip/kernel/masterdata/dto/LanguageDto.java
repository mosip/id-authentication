package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto class for Language.
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
	@NotNull(message = "{languageCode.notNull}")
	@Size(max = 3, min = 3, message = "{languageCode.size}")
	private String languageCode;

	/**
	 * Field for language name
	 */
	@NotNull(message = "{languageName.notNull}")
	@Size(max = 64, message = "{languageName.size}")
	private String languageName;

	/**
	 * Field for language family
	 */
	@Size(max = 64, message = "{languageName.size}")
	private String languageFamily;

	/**
	 * Field for language native name
	 */
	@Size(max = 64, message = "{languageName.size}")
	private String nativeName;

	/**
	 * Field for the status of data.
	 */
	@NotNull(message = "{isActive.notNull}")
	private Boolean isActive;

}
