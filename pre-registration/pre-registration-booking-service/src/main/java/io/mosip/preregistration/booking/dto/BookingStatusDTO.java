package io.mosip.preregistration.booking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingStatusDTO {
	private String preRegistrationId;
	private String bookingStatus;
	private String bookingMessage;
}
