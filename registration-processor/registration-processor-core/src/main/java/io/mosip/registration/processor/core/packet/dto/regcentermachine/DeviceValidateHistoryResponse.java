package io.mosip.registration.processor.core.packet.dto.regcentermachine;

import lombok.Data;

/**
 * Instantiates a new device validate history response.
 */
@Data
public class DeviceValidateHistoryResponse {

	/** The status. */
	private String status;

	/** The message. */
	private String message;

}
