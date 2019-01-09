package io.mosip.registration.entity.mastersync;

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

/**
 * @author Sreekar chukka
 * @since 1.0.0
 *
 */
@NamedNativeQueries({
		@NamedNativeQuery(name = "MasterRegistrationCenter.findRegistrationCentersByLat", query = "SELECT id, name, cntrtyp_code, addr_line1, addr_line2, addr_line3,number_of_kiosks,per_kiosk_process_time,center_end_time,center_start_time,latitude, longitude, location_code,holiday_loc_code,contact_phone,working_hours, lang_code, is_active, cr_by, cr_dtimes, upd_by,upd_dtimes, is_deleted, del_dtimes,time_zone,contact_person,lunch_start_time,lunch_end_time FROM (SELECT r.id, r.name, r.cntrtyp_code, r.addr_line1, r.addr_line2, r.addr_line3,r.number_of_kiosks,r.per_kiosk_process_time,r.center_end_time,r.center_start_time,r.latitude, r.longitude, r.location_code,r.holiday_loc_code,r.contact_phone, r.working_hours, r.lang_code,r.time_zone,r.contact_person,r.lunch_start_time,r.lunch_end_time,r.is_active, r.cr_by, r.cr_dtimes, r.upd_by,r.upd_dtimes, r.is_deleted, r.del_dtimes,(2 * 3961 * asin(sqrt((sin(radians((:latitude - CAST(r.latitude AS FLOAT)) / 2))) ^ 2 + cos(radians(CAST(r.latitude AS FLOAT))) * cos(radians(:latitude)) * (sin(radians((:longitude - CAST(r.longitude AS FLOAT)) / 2))) ^ 2))) AS distance FROM master.registration_center r) ss where distance < :proximitydistance and lang_code = :langcode and is_deleted=false order by distance asc;", resultClass = MasterRegistrationCenter.class) })

@Entity
@Table(name = "registration_center", schema = "reg")
public class MasterRegistrationCenter extends MasterSyncBaseEntity implements Serializable {

	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = -8541947587557590379L;

	/**
	 * the id of the registration center.
	 */
	@Id
	@Column(name = "id")
	private String id;

	/**
	 * the name of the registration center.
	 */
	@Column(name = "name")
	private String name;

	/**
	 * the center type code.
	 */
	@Column(name = "cntrtyp_code")
	private String centerTypeCode;

	/**
	 * the first address line.
	 */
	@Column(name = "addr_line1")
	private String addressLine1;

	/**
	 * the second address line.
	 */
	@Column(name = "addr_line2")
	private String addressLine2;

	/**
	 * the third address line.
	 */
	@Column(name = "addr_line3")
	private String addressLine3;

	/**
	 * the latitude of the registration center.
	 */
	@Column(name = "latitude")
	private String latitude;

	/**
	 * the longitude of the registration center.
	 */
	@Column(name = "longitude")
	private String longitude;

	/**
	 * the location code of the registration center.
	 */
	@Column(name = "location_code")
	private String locationCode;

	/**
	 * the {@link Location} reference.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "location_code", referencedColumnName = "code", insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false), })
	private MasterLocation location;

	/**
	 * the {@link RegistrationCenterType} reference.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "cntrtyp_code", referencedColumnName = "code", insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false), })
	
	private MasterRegistrationCenterType registrationCenterType;

	/**
	 * the contact phone of the registration center.
	 */
	@Column(name = "contact_phone")
	private String contactPhone;

	/**
	 * the number of kiosks.
	 */
	@Column(name = "number_of_kiosks")
	private Short numberOfKiosks;

	/**
	 * the holiday location code.
	 */
	@Column(name = "holiday_loc_code")
	private String holidayLocationCode;

	/**
	 * the number of working hours.
	 */
	@Column(name = "working_hours")
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
	@Column(name = "lang_code")
	private String languageCode;

	/**
	 * the timezone of the registration center.
	 */
	@Column(name = "time_zone")
	private String timeZone;

	/**
	 * the contact person of the registration center.
	 */
	@Column(name = "contact_person")
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

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the centerTypeCode
	 */
	public String getCenterTypeCode() {
		return centerTypeCode;
	}

	/**
	 * @param centerTypeCode the centerTypeCode to set
	 */
	public void setCenterTypeCode(String centerTypeCode) {
		this.centerTypeCode = centerTypeCode;
	}

	/**
	 * @return the addressLine1
	 */
	public String getAddressLine1() {
		return addressLine1;
	}

	/**
	 * @param addressLine1 the addressLine1 to set
	 */
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	/**
	 * @return the addressLine2
	 */
	public String getAddressLine2() {
		return addressLine2;
	}

	/**
	 * @param addressLine2 the addressLine2 to set
	 */
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	/**
	 * @return the addressLine3
	 */
	public String getAddressLine3() {
		return addressLine3;
	}

	/**
	 * @param addressLine3 the addressLine3 to set
	 */
	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the locationCode
	 */
	public String getLocationCode() {
		return locationCode;
	}

	/**
	 * @param locationCode the locationCode to set
	 */
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	/**
	 * @return the location
	 */
	public MasterLocation getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(MasterLocation location) {
		this.location = location;
	}

	/**
	 * @return the registrationCenterType
	 */
	public MasterRegistrationCenterType getRegistrationCenterType() {
		return registrationCenterType;
	}

	/**
	 * @param registrationCenterType the registrationCenterType to set
	 */
	public void setRegistrationCenterType(MasterRegistrationCenterType registrationCenterType) {
		this.registrationCenterType = registrationCenterType;
	}

	/**
	 * @return the contactPhone
	 */
	public String getContactPhone() {
		return contactPhone;
	}

	/**
	 * @param contactPhone the contactPhone to set
	 */
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	/**
	 * @return the numberOfKiosks
	 */
	public Short getNumberOfKiosks() {
		return numberOfKiosks;
	}

	/**
	 * @param numberOfKiosks the numberOfKiosks to set
	 */
	public void setNumberOfKiosks(Short numberOfKiosks) {
		this.numberOfKiosks = numberOfKiosks;
	}

	/**
	 * @return the holidayLocationCode
	 */
	public String getHolidayLocationCode() {
		return holidayLocationCode;
	}

	/**
	 * @param holidayLocationCode the holidayLocationCode to set
	 */
	public void setHolidayLocationCode(String holidayLocationCode) {
		this.holidayLocationCode = holidayLocationCode;
	}

	/**
	 * @return the workingHours
	 */
	public String getWorkingHours() {
		return workingHours;
	}

	/**
	 * @param workingHours the workingHours to set
	 */
	public void setWorkingHours(String workingHours) {
		this.workingHours = workingHours;
	}

	/**
	 * @return the perKioskProcessTime
	 */
	public LocalTime getPerKioskProcessTime() {
		return perKioskProcessTime;
	}

	/**
	 * @param perKioskProcessTime the perKioskProcessTime to set
	 */
	public void setPerKioskProcessTime(LocalTime perKioskProcessTime) {
		this.perKioskProcessTime = perKioskProcessTime;
	}

	/**
	 * @return the centerStartTime
	 */
	public LocalTime getCenterStartTime() {
		return centerStartTime;
	}

	/**
	 * @param centerStartTime the centerStartTime to set
	 */
	public void setCenterStartTime(LocalTime centerStartTime) {
		this.centerStartTime = centerStartTime;
	}

	/**
	 * @return the centerEndTime
	 */
	public LocalTime getCenterEndTime() {
		return centerEndTime;
	}

	/**
	 * @param centerEndTime the centerEndTime to set
	 */
	public void setCenterEndTime(LocalTime centerEndTime) {
		this.centerEndTime = centerEndTime;
	}

	/**
	 * @return the languageCode
	 */
	public String getLanguageCode() {
		return languageCode;
	}

	/**
	 * @param languageCode the languageCode to set
	 */
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	/**
	 * @return the timeZone
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * @param timeZone the timeZone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @return the contactPerson
	 */
	public String getContactPerson() {
		return contactPerson;
	}

	/**
	 * @param contactPerson the contactPerson to set
	 */
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	/**
	 * @return the lunchStartTime
	 */
	public LocalTime getLunchStartTime() {
		return lunchStartTime;
	}

	/**
	 * @param lunchStartTime the lunchStartTime to set
	 */
	public void setLunchStartTime(LocalTime lunchStartTime) {
		this.lunchStartTime = lunchStartTime;
	}

	/**
	 * @return the lunchEndTime
	 */
	public LocalTime getLunchEndTime() {
		return lunchEndTime;
	}

	/**
	 * @param lunchEndTime the lunchEndTime to set
	 */
	public void setLunchEndTime(LocalTime lunchEndTime) {
		this.lunchEndTime = lunchEndTime;
	}
	
	
	
}
