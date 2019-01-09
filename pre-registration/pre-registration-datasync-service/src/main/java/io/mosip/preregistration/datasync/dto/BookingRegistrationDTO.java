/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.datasync.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
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
	@JsonProperty("registration_center_id")
	@ApiModelProperty(value = "Registartion Center ID", position = 1)
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
