package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.BlacklistedWordsDto;
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
