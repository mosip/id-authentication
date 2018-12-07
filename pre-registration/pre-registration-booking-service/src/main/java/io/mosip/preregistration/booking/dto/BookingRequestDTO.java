package io.mosip.preregistration.booking.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

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
