package io.mosip.preregistration.booking.serviceimpl.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Getter
@Setter
public class MultiBookingRequestDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4081838636811604986L;
	/**
	 * pre-Registration Id
	 */
	@ApiModelProperty(value = "Pre-Registration ID", position = 1)
	private String preRegistrationId;
	/**
	 * registration Center Id
	 */
	@JsonProperty("registration_center_id")
	@ApiModelProperty(value = "Registration Center ID", position = 1)
	private String registrationCenterId;
	/**
	 * booked Date Time
	 */
	@JsonProperty("appointment_date")
	@ApiModelProperty(value = "Appointment Date", position = 2)
	private String regDate;
	/**
	 * booked from Time Slot
	 */
	@JsonProperty("time_slot_from")
	@ApiModelProperty(value = "From Time Slot", position = 3)
	private String slotFromTime;
	/**
	 * booked to Time Slot
	 */
	@JsonProperty("time_slot_to")
	@ApiModelProperty(value = "To Time Slot", position = 4)
	private String slotToTime;

}