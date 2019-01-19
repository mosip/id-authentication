package io.mosip.registration.dto.mastersync;

import java.time.LocalTime;

/**
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */

public class RegistrationCenterDto extends MasterSyncBaseDto{

	private String id;

	private String name;

	private String centerTypeCode;

	private String addressLine1;

	private String addressLine2;

	private String addressLine3;

	private String latitude;

	private String longitude;

	private String locationCode;

	private String holidayLocationCode;

	private String contactPhone;

	private Short numberOfStations;

	private String workingHours;

	private String languageCode;

	private Short numberOfKiosks;

	private LocalTime perKioskProcessTime;

	private LocalTime centerStartTime;

	private LocalTime centerEndTime;

	private String timeZone;

	private String contactPerson;

	private LocalTime lunchStartTime;

	private LocalTime lunchEndTime;

	private Boolean isActive;

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
	 * @return the numberOfStations
	 */
	public Short getNumberOfStations() {
		return numberOfStations;
	}

	/**
	 * @param numberOfStations the numberOfStations to set
	 */
	public void setNumberOfStations(Short numberOfStations) {
		this.numberOfStations = numberOfStations;
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

	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	

}
