package io.mosip.preregistration.booking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingStatusDTO {
	private String pre_registration_id;
	private String booking_status;
	private String booking_message;
}
