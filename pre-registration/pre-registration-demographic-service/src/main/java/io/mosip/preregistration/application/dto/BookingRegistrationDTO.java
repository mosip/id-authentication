/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * This DTO class is used to accept the values from Booking service during the
 * Rest call.
 * 
 * @author Jagadishwari S
 * @since 1.0.0
 */
@Getter
@Setter
public class BookingRegistrationDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7286592087727520299L;
	/**
	 * registration Center Id
	 */
	private String registration_center_id;

	/**
	 * booked Date
	 */
	private String reg_date;

	/**
	 * booked From Time
	 */
	@JsonProperty("time_slot_from")
	private String slotFromTime;

	/**
	 * booked To Time
	 */
	@JsonProperty("time_slot_to")
	private String slotToTime;
}
