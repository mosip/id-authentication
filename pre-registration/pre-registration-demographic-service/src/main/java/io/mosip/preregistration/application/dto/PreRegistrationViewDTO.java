package io.mosip.preregistration.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * View registration response DTO
 * 
 * @author M1037462
 * 
 */

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PreRegistrationViewDTO {
	private String preId;
	private String fullname;
	private String appointmentDate;
	private String statusCode;
	private BookingRegistrationDTO bookingRegistrationDTO;
}
