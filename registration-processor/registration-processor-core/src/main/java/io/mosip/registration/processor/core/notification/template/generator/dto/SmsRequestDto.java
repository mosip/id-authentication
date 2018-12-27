package io.mosip.registration.processor.core.notification.template.generator.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * The DTO class for sms notification request.
 * 
 * @author Alok
 * @since 1.0.0
 *
 */
@Data
public class SmsRequestDto {

	/**
	 * Contact number of recipient.
	 */
	
	@NotBlank
	private String number;
	
	/**
	 * Message need to send.
	 */
	
	@NotBlank
	private String message;

}
