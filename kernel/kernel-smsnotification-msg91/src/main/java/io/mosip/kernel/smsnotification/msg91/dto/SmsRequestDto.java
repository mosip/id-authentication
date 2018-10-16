package io.mosip.kernel.smsnotification.msg91.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * The DTO class for sms notification request.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
public class SmsRequestDto {

	/**
	 * Contact number of recipient.
	 */
	@NotNull
	@NotEmpty
	private String number;
	
	/**
	 * Message need to send.
	 */
	@NotNull
	@NotEmpty
	private String message;

}
