package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.IdTypeDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for id types response.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdTypeResponseDto {
	/**
	 * List of id types.
	 */
	private List<IdTypeDto> idtypes;
}
