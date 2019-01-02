package io.mosip.preregistration.booking.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
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
	@ApiModelProperty(value = "Pre-Registration ID", position = 1)
	private String preRegistrationId;
	/**
	 * Old Booking Details
	 */
	@ApiModelProperty(value = "Old Booking Data", position = 2)
	private BookingRegistrationDTO oldBookingDetails;
	/**
	 * New Booking Details
	 */
	@ApiModelProperty(value = "New Booking Data", position = 3)
	private BookingRegistrationDTO newBookingDetails;
}
