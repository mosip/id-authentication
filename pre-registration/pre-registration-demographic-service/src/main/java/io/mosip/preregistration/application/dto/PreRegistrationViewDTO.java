/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * View registration response DTO
 * 
 * @author Rupika
 * @since 1.0.0
 */

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PreRegistrationViewDTO {
	private String preId;
	private String fullname;
	private String statusCode;
	private BookingRegistrationDTO bookingRegistrationDTO;
}
