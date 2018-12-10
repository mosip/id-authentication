package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.RegistrationCenterUserMachineMappingHistoryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto for response to user for user machine mappings
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 */
@Data


public class RegistrationCenterUserMachineMappingHistoryResponseDto {
	/**
	 *  List of registration centers
	 */
	private List<RegistrationCenterUserMachineMappingHistoryDto> registrationCenters;
}
