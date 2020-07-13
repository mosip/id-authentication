package io.mosip.authentication.core.dto;

import io.mosip.authentication.core.indauth.dto.DigitalId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * The Class ValidateDeviceDto.
 * @author Manoj SP
 */

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Data

/**
 * Instantiates a new validate device DTO.
 */
@NoArgsConstructor

/**
 * Instantiates a new validate device DTO.
 *
 * @param deviceCode the device code
 * @param digitalId the digital id
 * @param deviceServiceVersion the device service version
 * @param timestamp the timestamp
 * @param purpose the purpose
 */
@AllArgsConstructor
public class ValidateDeviceDTO {

	/** The device code. */
	private String deviceCode;

	/** The digital id. */
	private DigitalId digitalId;

	/** The device service version. */
	private String deviceServiceVersion;
	
	/** The timestamp. */
	private String timeStamp;
	
	/** The purpose. */
	private String purpose;
}
