package io.mosip.preregistration.booking.serviceimpl.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemographicBookingRightJoin implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6964396778095509912L;

	/** Status of the preid */
	private String statusCode;
	
	/** Registration center id. */
	private String registrationCenterId;

	/** Slot from time. */
	private LocalTime slotFromTime;

	/** Slot to time. */
	private LocalTime slotToTime;

	/** Appointment date. */
	private LocalDate regDate;


}
