package io.mosip.kernel.masterdata.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Blacklisted words response Dto
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 06-11-2018
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistedWordsResponseDto {
	private List<BlacklistedWordsDto> blacklistedwords;
}
