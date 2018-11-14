package io.mosip.registration.entity;

import java.sql.Time;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * RegistrationCenter entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "registration_center")
public class RegistrationCenter extends RegistrationCommonFields {

	@Id
	@Column(name = "id")
	private String centerId;
	@Column(name = "name")
	private String centerName;
	@Column(name = "cntrtyp_code")
	private String cntrTypCode;
	@Column(name = "addr_line1")
	private String addrLine1;
	@Column(name = "addr_line2")
	private String addrLine2;
	@Column(name = "addr_line3")
	private String addrLine3;
	@Column(name = "latitude")
	private String latitude;
	@Column(name = "longitude")
	private String longitude;
	@Column(name = "location_Code")
	private String locationCode;
	@Column(name = "contact_phone")
	private String contactPhone;
	@Column(name = "number_of_kiosks")
	private Integer numberOfKiosks;
	@Column(name = "working_hours")
	private String workingHours;
	@Column(name = "per_kiosk_process_time")
	private Time perKioskProcessTime;
	@Column(name = "process_start_time")
	private Time processStartTime;
	@Column(name = "process_end_time")
	private Time processEndTime;
	@Column(name = "holiday_loc_code")
	private String holidayLocCode;
	@Column(name = "lang_code")
	private String langCode;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp delDtimes;
	
	@OneToOne(fetch = FetchType.EAGER, mappedBy = "registrationUserDetail")
	private RegistrationCenterUser registrationCenterUser;

	/**
	 * @return the centerId
	 */
	public String getCenterId() {
		return centerId;
	}

	/**
	 * @param centerId
	 *            the centerId to set
	 */
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	/**
	 * @return the centerName
	 */
	public String getCenterName() {
		return centerName;
	}

	/**
	 * @param centerName
	 *            the centerName to set
	 */
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}

	/**
	 * @return the cntrTypCode
	 */
	public String getCntrTypCode() {
		return cntrTypCode;
	}

	/**
	 * @param cntrTypCode
	 *            the cntrTypCode to set
	 */
	public void setCntrTypCode(String cntrTypCode) {
		this.cntrTypCode = cntrTypCode;
	}

	/**
	 * @return the addrLine1
	 */
	public String getAddrLine1() {
		return addrLine1;
	}

	/**
	 * @param addrLine1
	 *            the addrLine1 to set
	 */
	public void setAddrLine1(String addrLine1) {
		this.addrLine1 = addrLine1;
	}

	/**
	 * @return the addrLine2
	 */
	public String getAddrLine2() {
		return addrLine2;
	}

	/**
	 * @param addrLine2
	 *            the addrLine2 to set
	 */
	public void setAddrLine2(String addrLine2) {
		this.addrLine2 = addrLine2;
	}

	/**
	 * @return the addrLine3
	 */
	public String getAddrLine3() {
		return addrLine3;
	}

	/**
	 * @param addrLine3
	 *            the addrLine3 to set
	 */
	public void setAddrLine3(String addrLine3) {
		this.addrLine3 = addrLine3;
	}

	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
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
	 * @param longitude
	 *            the longitude to set
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
	 * @param locationCode
	 *            the locationCode to set
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
	 * @param contactPhone
	 *            the contactPhone to set
	 */
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	/**
	 * @return the numberOfKiosks
	 */
	public Integer getNumberOfKiosks() {
		return numberOfKiosks;
	}

	/**
	 * @param numberOfKiosks
	 *            the numberOfKiosks to set
	 */
	public void setNumberOfKiosks(Integer numberOfKiosks) {
		this.numberOfKiosks = numberOfKiosks;
	}

	/**
	 * @return the workingHours
	 */
	public String getWorkingHours() {
		return workingHours;
	}

	/**
	 * @param workingHours
	 *            the workingHours to set
	 */
	public void setWorkingHours(String workingHours) {
		this.workingHours = workingHours;
	}

	/**
	 * @return the perKioskProcessTime
	 */
	public Time getPerKioskProcessTime() {
		return perKioskProcessTime;
	}

	/**
	 * @param perKioskProcessTime
	 *            the perKioskProcessTime to set
	 */
	public void setPerKioskProcessTime(Time perKioskProcessTime) {
		this.perKioskProcessTime = perKioskProcessTime;
	}

	/**
	 * @return the processStartTime
	 */
	public Time getProcessStartTime() {
		return processStartTime;
	}

	/**
	 * @param processStartTime
	 *            the processStartTime to set
	 */
	public void setProcessStartTime(Time processStartTime) {
		this.processStartTime = processStartTime;
	}

	/**
	 * @return the processEndTime
	 */
	public Time getProcessEndTime() {
		return processEndTime;
	}

	/**
	 * @param processEndTime
	 *            the processEndTime to set
	 */
	public void setProcessEndTime(Time processEndTime) {
		this.processEndTime = processEndTime;
	}

	/**
	 * @return the holidayLocCode
	 */
	public String getHolidayLocCode() {
		return holidayLocCode;
	}

	/**
	 * @param holidayLocCode
	 *            the holidayLocCode to set
	 */
	public void setHolidayLocCode(String holidayLocCode) {
		this.holidayLocCode = holidayLocCode;
	}

	/**
	 * @return the langCode
	 */
	public String getLangCode() {
		return langCode;
	}

	/**
	 * @param langCode
	 *            the langCode to set
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	/**
	 * @return the isDeleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted
	 *            the isDeleted to set
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * @return the delDtimes
	 */
	public Timestamp getDelDtimes() {
		return delDtimes;
	}

	/**
	 * @param delDtimes
	 *            the delDtimes to set
	 */
	public void setDelDtimes(Timestamp delDtimes) {
		this.delDtimes = delDtimes;
	}

}
