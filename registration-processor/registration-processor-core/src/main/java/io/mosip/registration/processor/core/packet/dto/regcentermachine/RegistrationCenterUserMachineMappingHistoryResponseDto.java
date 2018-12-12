package io.mosip.registration.processor.core.packet.dto.regcentermachine;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationCenterUserMachineMappingHistoryResponseDto {
	/**
	 *  List of registration centers
	 */
	private List<RegistrationCenterUserMachineMappingHistoryDto> registrationCenters;
}