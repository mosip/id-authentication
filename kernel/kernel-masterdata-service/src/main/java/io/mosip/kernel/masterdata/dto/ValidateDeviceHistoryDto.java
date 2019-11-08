package io.mosip.kernel.masterdata.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiParam;
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
public class ValidateDeviceHistoryDto {

	/** The device code. */
	@NotBlank
	private String deviceCode;

	/** The digital id. */
	@Valid
	private DigitalIdDto digitalId;

	/** The device service version. */
	@NotBlank 
	private String deviceServiceVersion;
	
	@NotBlank
	@ApiParam(value="2019-09-09T09:09:09.000Z",required=true)
	private String timeStamp;
}
