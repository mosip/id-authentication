package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * Blacklisted word DTO.
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data
public class BlacklistedWordsDto {
	@NotNull
	@Size(min = 1, max = 128)
	private String word;
	@NotNull
	@Size(min = 1, max = 3)
	private String langCode;
	@Size(min = 1, max = 256)
	private String description;
	@NotNull
	private Boolean isActive;
}
