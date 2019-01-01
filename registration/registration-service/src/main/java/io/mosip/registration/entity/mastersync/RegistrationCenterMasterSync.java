package io.mosip.registration.entity.mastersync;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import io.mosip.registration.entity.RegistrationCommonFields;

/**
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
@NamedNativeQueries({
		@NamedNativeQuery(name = "RegistrationCenter.findRegistrationCentersByLat", query = "SELECT id, name, cntrtyp_code, addr_line1, addr_line2, addr_line3,number_of_kiosks,per_kiosk_process_time,process_end_time,process_start_time,latitude, longitude, location_code,holiday_loc_code,contact_phone,working_hours, lang_code, is_active, cr_by, cr_dtimes, upd_by,upd_dtimes, is_deleted, del_dtimes FROM (SELECT r.id, r.name, r.cntrtyp_code, r.addr_line1, r.addr_line2, r.addr_line3,r.number_of_kiosks,r.per_kiosk_process_time,r.process_end_time,r.process_start_time,r.latitude, r.longitude, r.location_code,r.holiday_loc_code,r.contact_phone, r.working_hours, r.lang_code,r.is_active, r.cr_by, r.cr_dtimes, r.upd_by,r.upd_dtimes, r.is_deleted, r.del_dtimes,(2 * 3961 * asin(sqrt((sin(radians((:latitude - CAST(r.latitude AS FLOAT)) / 2))) ^ 2 + cos(radians(CAST(r.latitude AS FLOAT))) * cos(radians(:latitude)) * (sin(radians((:longitude - CAST(r.longitude AS FLOAT)) / 2))) ^ 2))) AS distance FROM master.registration_center r) ss where distance < :proximitydistance and lang_code = :langcode and is_deleted=false and is_active=true order by distance asc;", resultClass = RegistrationCenterMasterSync.class) })

@Entity
@Table(name = "registration_center", schema = "reg")
public class RegistrationCenterMasterSync extends RegistrationCommonFields implements Serializable {

	private static final long serialVersionUID = -8541947587557590379L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "name")
	private String name;

	@Column(name = "cntrtyp_code")
	private String centerTypeCode;

	@Column(name = "addr_line1")
	private String addressLine1;

	@Column(name = "addr_line2")
	private String addressLine2;

	@Column(name = "addr_line3")
	private String addressLine3;

	@Column(name = "latitude")
	private String latitude;

	@Column(name = "longitude")
	private String longitude;

	@Column(name = "location_code")
	private String locationCode;

	@Column(name = "contact_phone")
	private String contactPhone;

	@Column(name = "number_of_kiosks")
	private Short numberOfKiosks;

	@Column(name = "holiday_loc_code")
	private String holidayLocationCode;

	@Column(name = "working_hours")
	private String workingHours;

	@Column(name = "per_kiosk_process_time")
	private LocalTime perKioskProcessTime;

	@Column(name = "process_start_time")
	private LocalTime processStartTime;

	@Column(name = "process_end_time")
	private LocalTime processEndTime;

	@Column(name = "lang_code")
	private String languageCode;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime deletedtimes;

	@OneToOne(mappedBy = "code", cascade = CascadeType.ALL)

	private LocationHierarcyLevel location;

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
	 * @return the processStartTime
	 */
	public LocalTime getProcessStartTime() {
		return processStartTime;
	}

	/**
	 * @param processStartTime the processStartTime to set
	 */
	public void setProcessStartTime(LocalTime processStartTime) {
		this.processStartTime = processStartTime;
	}

	/**
	 * @return the processEndTime
	 */
	public LocalTime getProcessEndTime() {
		return processEndTime;
	}

	/**
	 * @param processEndTime the processEndTime to set
	 */
	public void setProcessEndTime(LocalTime processEndTime) {
		this.processEndTime = processEndTime;
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
	 * @return the isDeleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted the isDeleted to set
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * @return the deletedtimes
	 */
	public LocalDateTime getDeletedtimes() {
		return deletedtimes;
	}

	/**
	 * @param deletedtimes the deletedtimes to set
	 */
	public void setDeletedtimes(LocalDateTime deletedtimes) {
		this.deletedtimes = deletedtimes;
	}

	/**
	 * @return the location
	 */
	public LocationHierarcyLevel getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(LocationHierarcyLevel location) {
		this.location = location;
	}

}
