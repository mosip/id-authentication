package io.mosip.registration.processor.core.packet.dto.regcentermachine;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
	
/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Data

/**
 * Instantiates a new registration center user machine mapping history response dto.
 *
 * @param registrationCenters the registration centers
 */
@AllArgsConstructor

/**
 * Instantiates a new registration center user machine mapping history response dto.
 */
@NoArgsConstructor
public class RegistrationCenterUserMachineMappingHistoryResponseDto {
	
	/**  List of registration centers. */
	private List<RegistrationCenterUserMachineMappingHistoryDto> registrationCenters;
}