package io.mosip.kernel.masterdata.dto;

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
public class ValidateDeviceDto {

	/** The device code. */
	@NotBlank
	private String deviceCode;

	/** The digital id. */
	@Valid
	private DigitalIdDto digitalId;

	/** The device service version. */
	@NotBlank 
	private String deviceServiceVersion;
}
