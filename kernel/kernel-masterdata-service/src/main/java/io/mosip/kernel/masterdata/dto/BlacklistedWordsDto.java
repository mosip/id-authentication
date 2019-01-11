package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.mosip.kernel.masterdata.validator.ValidLangCode;
import lombok.Data;

/**
 * Blacklisted word DTO.
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data
public class BlacklistedWordsDto {
	@NotBlank
	@Size(min = 1, max = 128)
	private String word;
	
	@ValidLangCode
	@NotBlank
	@Size(min = 1, max = 3)
	private String langCode;
	@Size(min = 1, max = 256)
	private String description;
	@NotNull
	private Boolean isActive;
}
