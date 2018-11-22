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
	@NotNull(message = "{code.notNull}")
	@Size(max = 3, min = 3, message = "{code.size}")
	private String code;

	/**
	 * Field for language name
	 */
	@NotNull(message = "{name.notNull}")
	@Size(max = 64, message = "{name.size}")
	private String name;

	/**
	 * Field for language family
	 */
	@Size(max = 64, message = "{family.size}")
	private String family;

	/**
	 * Field for language native name
	 */
	@Size(max = 64, message = "{nativeName.size}")
	private String nativeName;

	/**
	 * Field for the status of data.
	 */
	@NotNull(message = "{isActive.notNull}")
	private Boolean isActive;

}
