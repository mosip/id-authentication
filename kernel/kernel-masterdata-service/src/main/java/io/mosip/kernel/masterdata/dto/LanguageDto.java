package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
	@NotNull
	@Size(min = 1, max = 3)
	private String code;

	/**
	 * Field for language name
	 */
	@NotNull
	@Size(min = 1, max = 64)
	private String name;

	/**
	 * Field for language family
	 */
	@Size(min = 0, max = 64)
	private String family;

	/**
	 * Field for language native name
	 */
	@Size(min = 0, max = 64)
	private String nativeName;

	/**
	 * Field for the status of data.
	 */
	@NotNull
	private Boolean isActive;

}
