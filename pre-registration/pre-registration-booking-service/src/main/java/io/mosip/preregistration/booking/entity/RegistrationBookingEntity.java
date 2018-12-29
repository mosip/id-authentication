package io.mosip.preregistration.booking.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedQuery;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reg_appointment", schema = "prereg")
@NamedQuery(name = "RegistrationBookingEntity.existsByPreIdandStatusCode", query = "SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM RegistrationBookingEntity u WHERE u.bookingPK.preregistrationId = ?1 and u.statusCode = ?2")
@NamedQuery(name="RegistrationBookingEntity.findPreIdAndStatusCode",query="SELECT r from RegistrationBookingEntity r WHERE r.bookingPK.preregistrationId = ?1 and r.statusCode=?2")
public class RegistrationBookingEntity implements Serializable {

	private static final long serialVersionUID = 7886669943207769620L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;
	
	@Embedded
	private RegistrationBookingPK bookingPK;

	@Column(name = "regcntr_id")
	private String registrationCenterId;

	@Column(name = "slot_from_time")
	private LocalTime slotFromTime;

	@Column(name = "slot_to_time")
	private LocalTime slotToTime;

	@Column(name = "appointment_date")
	private LocalDate regDate;

	@Column(name = "status_code")
	private String statusCode;

	@Column(name = "lang_code")
	private String langCode;

	@Column(name = "cr_by")
	private String crBy;

	@Column(name = "cr_dtimes")
	private LocalDateTime crDate;

	@Column(name = "upd_by")
	private String upBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updDate;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime delTime;

}
