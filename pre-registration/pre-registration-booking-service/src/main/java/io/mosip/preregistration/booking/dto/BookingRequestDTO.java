package io.mosip.preregistration.booking.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * @author M1046129
 *
 */
@Getter
@Setter
public class BookingRequestDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3339740008361919496L;
	/**
	 * pre-Registration Id
	 */
	private String pre_registration_id;
	/**
	 * Old Booking Details
	 */
	private BookingRegistrationDTO oldBookingDetails;
	/**
	 * New Booking Details
	 */
	private BookingRegistrationDTO newBookingDetails;
}
