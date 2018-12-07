package io.mosip.preregistration.booking.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRegistrationDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7286592087727520299L;
	/**
	 * registration Center Id
	 */
	private String registration_center_id;
	/**
	 * booked Date Time
	 */
	private String reg_date;
	/**
	 * booked Time Slot
	 */
	@JsonProperty("time-slot-from")
	private String slotFromTime;
	
	@JsonProperty("time-slot-to")
	private String slotToTime;
}
