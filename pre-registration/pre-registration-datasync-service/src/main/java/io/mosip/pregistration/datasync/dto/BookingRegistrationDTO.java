/* 
 * Copyright
 * 
 */
package io.mosip.pregistration.datasync.dto;

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
	/**
	 * 
	 */
	private static final long serialVersionUID = -7286592087727520299L;
	/**
	 * registration Center Id
	 */
	private String registrationCenterId;
	/**
	 * booked Date Time
	 */
	private String regDate;
	/**
	 * booked Time Slot
	 */
	@JsonProperty("time_slot_from")
	private String slotFromTime;
	
	@JsonProperty("time_slot_to")
	private String slotToTime;
}
