package io.mosip.preregistration.booking.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
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
	@Override
	public int hashCode() {
		return 1;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookingRegistrationDTO other = (BookingRegistrationDTO) obj;
		if (regDate == null) {
			if (other.regDate != null)
				return false;
		} else if (!regDate.equals(other.regDate))
			return false;
		if (registrationCenterId == null) {
			if (other.registrationCenterId != null)
				return false;
		} else if (!registrationCenterId.equals(other.registrationCenterId))
			return false;
		if (slotFromTime == null) {
			if (other.slotFromTime != null)
				return false;
		} else if (!slotFromTime.equals(other.slotFromTime))
			return false;
		if (slotToTime == null) {
			if (other.slotToTime != null)
				return false;
		} else if (!slotToTime.equals(other.slotToTime))
			return false;
		return true;
	}
}
