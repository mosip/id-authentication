package io.mosip.preregistration.booking.dto;

import java.util.List;

import io.mosip.preregistration.booking.serviceimpl.dto.BookingStatusDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingStatus {
	
	List<BookingStatusDTO> bookingStatusResponse;

}
