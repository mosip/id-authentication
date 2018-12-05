package io.mosip.preregistration.booking.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

/**
 * @author M1046129
 *
 */
@Getter
@Setter
public class BookingDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6489834223858096784L;
	/**
	 * 
	 */
	private String id;
	/**
	 * 
	 */
	private String ver;
	/**
	 * 
	 */
	private Timestamp reqTime;
	/**
	 * To accept preregid, regcenterid, timeslot and booked date time
	 */
	private BookingRequestDTO request;
}
