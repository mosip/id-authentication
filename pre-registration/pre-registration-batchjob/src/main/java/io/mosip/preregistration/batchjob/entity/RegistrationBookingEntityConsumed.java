/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjob.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Setter;

/**
 * This entity class defines the database table details for Booking application.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Setter
@Entity
@Table(name = "reg_appointment_consumed", schema = "prereg")
public class RegistrationBookingEntityConsumed implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7698541520991836493L;

	/** Id. */
	@Id
	@Column(name = "id")
	private String id;
	
	/** Booking primary Key. */
	@Embedded
	private RegistrationBookingPKConsumed bookingPK;

	/** Registration center id. */
	@Column(name = "regcntr_id")
	private String registrationCenterId;

	/** Slot from time. */
	@Column(name = "slot_from_time")
	private LocalTime slotFromTime;

	/** Slot to time. */
	@Column(name = "slot_to_time")
	private LocalTime slotToTime;

	/** Appointment date. */
	@Column(name = "appointment_date")
	private LocalDate regDate;

	/** Language code. */
	@Column(name = "lang_code")
	private String langCode;

	/** Created by. */
	@Column(name = "cr_by")
	private String crBy;

	/** Created date time. */
	@Column(name = "cr_dtimes")
	private LocalDateTime crDate;

	/** Created by. */
	@Column(name = "upd_by")
	private String upBy;

	/** Updated date time. */
	@Column(name = "upd_dtimes")
	private LocalDateTime updDate;


}
