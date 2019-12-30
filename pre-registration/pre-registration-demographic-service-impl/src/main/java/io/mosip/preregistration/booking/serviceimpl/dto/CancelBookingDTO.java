package io.mosip.preregistration.booking.serviceimpl.dto;


import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelBookingDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * registration Center Id
	 */
	@JsonProperty("registration_center_id")
	@ApiModelProperty(value = "Registration Center ID", position = 2)
	private String registrationCenterId;
	/**
	 * booked Date Time
	 */
	@JsonProperty("appointment_date")
	@ApiModelProperty(value = "Appointment Date", position = 3)
	private String regDate;
	/**
	 * booked Time Slot
	 */
	@JsonProperty("time_slot_from")
	@ApiModelProperty(value = "From Time Slot", position = 4)
	private String slotFromTime;
	
	/**
	 * booked to Time Slot
	 */
	@JsonProperty("time_slot_to")
	@ApiModelProperty(value = "To Time Slot", position = 5)
	private String slotToTime;
}
