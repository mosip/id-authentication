package io.mosip.authentication.core.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * The Class ValidateDeviceDto.
 * @author Srinivasan
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateDeviceDTO {

	/** The device code. */
	@NotBlank
	private String deviceCode;

	/** The digital id. */
	@Valid
	private DigitalIdDTO digitalId;

	/** The device service version. */
	@NotBlank 
	private String deviceServiceVersion;
}
