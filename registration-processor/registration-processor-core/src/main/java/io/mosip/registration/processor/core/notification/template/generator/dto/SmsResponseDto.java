package io.mosip.registration.processor.core.notification.template.generator.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * The DTO class for sms notification response.
 * 
 * @author Alok 
 * @since 1.0.0
 *
 */
@Data
public class SmsResponseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Response status.
	 */
	private String status;
	
	/**
	 * Response message
	 */
	private String message;
}
