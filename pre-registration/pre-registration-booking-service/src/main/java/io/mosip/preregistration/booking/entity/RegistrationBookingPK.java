package io.mosip.preregistration.booking.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

/** 
 * To define the composite primary key
 * @author M1046129
 *
 */
@Embeddable
@Getter
@Setter
public class RegistrationBookingPK implements Serializable{
	
	private static final long serialVersionUID = -4604149554069906933L;

	/**
	 * Pre registration Id
	 */
	@Column(name="prereg_id")
	private String preregistrationId;
	
	/**
	 * Booking date and time
	 */
	@Column(name="booking_dtimes")
	private Timestamp bookingDateTime;
}
