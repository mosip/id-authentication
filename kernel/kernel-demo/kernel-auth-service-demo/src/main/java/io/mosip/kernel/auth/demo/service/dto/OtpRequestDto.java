package io.mosip.kernel.auth.demo.service.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * The DTO class for sms notification request.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
public class OtpRequestDto {

	/**
	 * Contact number of recipient.
	 */
	
	@NotBlank
	private String key;
	

}
