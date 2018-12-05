package io.mosip.kernel.synchandler.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "registration_center_h", schema = "master")
public class RegistrationCenterHistory extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -8541947587557590379L;

	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	private String id;

	@Column(name = "name", nullable = false, length = 128)
	private String name;

	@Column(name = "cntrtyp_code", length = 36)
	private String centerTypeCode;

	@Column(name = "addr_line1", length = 256)
	private String addressLine1;

	@Column(name = "addr_line2", length = 256)
	private String addressLine2;

	@Column(name = "addr_line3", length = 256)
	private String addressLine3;

	@Column(name = "latitude", length = 32)
	private String latitude;

	@Column(name = "longitude", length = 32)
	private String longitude;

	@Column(name = "location_code", nullable = false, length = 36)
	private String locationCode;

	@Column(name = "contact_phone", length = 16)
	private String contactPhone;

	@Column(name = "number_of_kiosks")
	private Short numberOfKiosks;

	@Column(name = "holiday_loc_code", nullable = false, length = 36)
	private String holidayLocationCode;

	@Column(name = "working_hours", length = 32)
	private String workingHours;

	@Column(name = "per_kiosk_process_time")
	private LocalTime perKioskProcessTime;

	@Column(name = "center_start_time")
	private LocalTime centerStartTime;

	@Column(name = "center_end_time")
	private LocalTime centerEndTime;

	@Column(name = "lang_code", nullable = false, length = 3)
	private String languageCode;

	@Column(name = "time_zone", length = 64)
	private String timeZone;

	@Column(name = "contact_person", length = 128)
	private String contactPerson;

	@Column(name = "lunch_start_time")
	private LocalTime lunchStartTime;

	@Column(name = "lunch_end_time")
	private LocalTime lunchEndTime;

	@OneToOne(mappedBy = "code", cascade = CascadeType.ALL)
	private LocationHierarcyLevel location;

	@Column(name = "eff_dtimes", nullable = false)
	private LocalDateTime effectivetimes;
}
