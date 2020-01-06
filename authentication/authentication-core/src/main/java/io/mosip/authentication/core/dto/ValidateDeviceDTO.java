package io.mosip.authentication.core.dto;

import io.mosip.authentication.core.indauth.dto.DigitalId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * The Class ValidateDeviceDto.
 * @author Manoj SP
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateDeviceDTO {

	/** The device code. */
	private String deviceCode;

	/** The digital id. */
	private DigitalId digitalId;

	/** The device service version. */
	private String deviceServiceVersion;
}
