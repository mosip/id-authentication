package io.mosip.preregistration.booking.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author M1046129
 *
 */
@Getter
@Setter
@ToString
public class BookingDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6489834223858096784L;
	/**
	 * id
	 */
	private String id;
	/**
	 * version
	 */
	private String ver;
	/**
	 * reqTime
	 */
	private String reqTime;
	/**
	 * To accept preregid, regcenterid, timeslot and booked date time
	 */
	private List<BookingRequestDTO> request;
}
