package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * blacklisted word Dto
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 06-11-2018
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistedWordsDto {
	private String word;
	private String description;
	private String langCode;
	private Boolean isActive;
}
