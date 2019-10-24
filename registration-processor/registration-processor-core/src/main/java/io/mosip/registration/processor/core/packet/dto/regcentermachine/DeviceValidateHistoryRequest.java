package io.mosip.registration.processor.core.packet.dto.regcentermachine;

import io.mosip.registration.processor.core.packet.dto.DigitalIdDto;
import lombok.Data;

/**
 * Instantiates a new device validate history request.
 */
@Data
public class DeviceValidateHistoryRequest {
	/** The device code. */
	private String deviceCode;

	/** The digital id. */
	private DigitalIdDto digitalId;

	/** The device service version. */
	private String deviceServiceVersion;

	/** The time stamp. */
	private String timeStamp;

}
