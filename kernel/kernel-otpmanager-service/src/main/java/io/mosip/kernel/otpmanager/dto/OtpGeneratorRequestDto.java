package io.mosip.kernel.otpmanager.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * The DTO class for OTP Generation request.
 * 
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @since 1.0.0
 * 
 */
@Data
public class OtpGeneratorRequestDto {
	/**
	 * The key against which OTP needs to be generated.
	 */
	@NotNull
	@Size(min = 3, max = 64)
	private String key;
}
