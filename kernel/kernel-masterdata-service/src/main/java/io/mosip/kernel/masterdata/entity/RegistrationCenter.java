package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Dharmesh Khandelwal
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@NamedNativeQueries({
		@NamedNativeQuery(name = "RegistrationCenter.findRegistrationCentersByLat", query = "SELECT id, name, cntrtyp_code, addr_line1, addr_line2, addr_line3,number_of_kiosks,per_kiosk_process_time,center_end_time,center_start_time,latitude, longitude, location_code,holiday_loc_code,contact_phone,working_hours, lang_code, is_active, cr_by, cr_dtimes, upd_by,upd_dtimes, is_deleted, del_dtimes,time_zone,contact_person,lunch_start_time,lunch_end_time FROM (SELECT r.id, r.name, r.cntrtyp_code, r.addr_line1, r.addr_line2, r.addr_line3,r.number_of_kiosks,r.per_kiosk_process_time,r.center_end_time,r.center_start_time,r.latitude, r.longitude, r.location_code,r.holiday_loc_code,r.contact_phone, r.working_hours, r.lang_code,r.time_zone,r.contact_person,r.lunch_start_time,r.lunch_end_time,r.is_active, r.cr_by, r.cr_dtimes, r.upd_by,r.upd_dtimes, r.is_deleted, r.del_dtimes,(2 * 3961 * asin(sqrt((sin(radians((:latitude - CAST(r.latitude AS FLOAT)) / 2))) ^ 2 + cos(radians(CAST(r.latitude AS FLOAT))) * cos(radians(:latitude)) * (sin(radians((:longitude - CAST(r.longitude AS FLOAT)) / 2))) ^ 2))) AS distance FROM master.registration_center r) ss where distance < :proximitydistance and lang_code = :langcode and is_deleted=false order by distance asc;", resultClass = RegistrationCenter.class) })

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "registration_center", schema = "master")
public class RegistrationCenter extends BaseEntity implements Serializable {

	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = -8541947587557590379L;

	/**
	 * the id of the registration center.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	private String id;

	/**
	 * the name of the registration center.
	 */
	@Column(name = "name", nullable = false, length = 128)
	private String name;

	/**
	 * the center type code.
	 */
	@Column(name = "cntrtyp_code", length = 36)
	private String centerTypeCode;

	/**
	 * the first address line.
	 */
	@Column(name = "addr_line1", length = 256)
	private String addressLine1;

	/**
	 * the second address line.
	 */
	@Column(name = "addr_line2", length = 256)
	private String addressLine2;

	/**
	 * the third address line.
	 */
	@Column(name = "addr_line3", length = 256)
	private String addressLine3;

	/**
	 * the latitude of the registration center.
	 */
	@Column(name = "latitude", length = 32)
	private String latitude;

	/**
	 * the longitude of the registration center.
	 */
	@Column(name = "longitude", length = 32)
	private String longitude;

	/**
	 * the location code of the registration center.
	 */
	@Column(name = "location_code", length = 36)
	private String locationCode;

	/**
	 * the {@link Location} reference.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "location_code", referencedColumnName = "code", insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false), })
	private Location location;

	/**
	 * the {@link RegistrationCenterType} reference.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "cntrtyp_code", referencedColumnName = "code", insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false), })
	private RegistrationCenterType registrationCenterType;

	/**
	 * the contact phone of the registration center.
	 */
	@Column(name = "contact_phone", length = 16)
	private String contactPhone;

	/**
	 * the number of kiosks.
	 */
	@Column(name = "number_of_kiosks")
	private Short numberOfKiosks;

	/**
	 * the holiday location code.
	 */
	@Column(name = "holiday_loc_code", nullable = false, length = 36)
	private String holidayLocationCode;

	/**
	 * the number of working hours.
	 */
	@Column(name = "working_hours", length = 32)
	private String workingHours;

	/**
	 * the per kiosk process time.
	 */
	@Column(name = "per_kiosk_process_time")
	private LocalTime perKioskProcessTime;

	/**
	 * the start time of the registration center.
	 */
	@Column(name = "center_start_time")
	private LocalTime centerStartTime;

	/**
	 * the end time of the registration center.
	 */
	@Column(name = "center_end_time")
	private LocalTime centerEndTime;

	/**
	 * the language code of the registration center.
	 */
	@Column(name = "lang_code", nullable = false, length = 3)
	private String languageCode;

	/**
	 * the timezone of the registration center.
	 */
	@Column(name = "time_zone", length = 64)
	private String timeZone;

	/**
	 * the contact person of the registration center.
	 */
	@Column(name = "contact_person", length = 128)
	private String contactPerson;

	/**
	 * the lunch start time of the registration center.
	 */
	@Column(name = "lunch_start_time")
	private LocalTime lunchStartTime;

	/**
	 * the lunch end time of the registration center.
	 */
	@Column(name = "lunch_end_time")
	private LocalTime lunchEndTime;

}
