package io.mosip.registration.processor.core.notification.template.generator.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * The DTO class for sms notification request.
 * 
 * @author Alok
 * @since 1.0.0
 *
 */
@Data
public class SmsRequestDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Contact number of recipient.
	 */
	private String number;
	
	/**
	 * Message need to send.
	 */
	private String message;

}
