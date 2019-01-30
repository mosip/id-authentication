
package io.mosip.registration.processor.core.packet.dto.regcentermachine;

import java.util.List;

import lombok.Data;

/**
 * The Class RegistrationCenterDeviceHistoryResponseDto.
 *
 * @author Uday Kumar
 * @version 1.0.0
 */

/**
 * Instantiates a new registration center device history response dto.
 */
@Data
public class RegistrationCenterDeviceHistoryResponseDto {

	/** The registration center device history details. */
	private RegistrationCenterDeviceHistoryDto registrationCenterDeviceHistoryDetails;

	/** The errors. */
	private List<ErrorDTO> errors;
}
