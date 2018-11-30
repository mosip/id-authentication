package io.mosip.registration.dto;

import java.sql.Time;

/**
 * This class contains the Registration Center details.
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public class RegistrationCenterDetailDTO {

	private String registrationCenterId;
	private String registrationCenterName;
	private String regsitrationCenterTypeCode;
	private String registrationCenterAddrLine1;
	private String registrationCenterAddrLine2;
	private String registrationCenterAddrLine3;
	private String registrationCenterLatitude;
	private String registrationCenterLongitude;
	private String registrationCenterLocationCode;
	private int registrationCenterNumberOfStations;
	private String registrationCenterWorkingHours;
	private String registrationCenterContactPhone;
	private Integer registrationCenterNumberOfKiosks;
	private Time registrationCenterPerKioskProcessTime;
	private Time registrationCenterProcessStartTime;
	private Time registrationCenterProcessEndTime;
	private String registrationCenterHolidayLocCode;

	/**
	 * @return the registrationCenterId
	 */
	public String getRegistrationCenterId() {
		return registrationCenterId;
	}

	/**
	 * @param registrationCenterId
	 *            the registrationCenterId to set
	 */
	public void setRegistrationCenterId(String registrationCenterId) {
		this.registrationCenterId = registrationCenterId;
	}

	/**
	 * @return the registrationCenterName
	 */
	public String getRegistrationCenterName() {
		return registrationCenterName;
	}

	/**
	 * @param registrationCenterName
	 *            the registrationCenterName to set
	 */
	public void setRegistrationCenterName(String registrationCenterName) {
		this.registrationCenterName = registrationCenterName;
	}

	/**
	 * @return the regsitrationCenterTypeCode
	 */
	public String getRegsitrationCenterTypeCode() {
		return regsitrationCenterTypeCode;
	}

	/**
	 * @param regsitrationCenterTypeCode
	 *            the regsitrationCenterTypeCode to set
	 */
	public void setRegsitrationCenterTypeCode(String regsitrationCenterTypeCode) {
		this.regsitrationCenterTypeCode = regsitrationCenterTypeCode;
	}

	/**
	 * @return the registrationCenterAddrLine1
	 */
	public String getRegistrationCenterAddrLine1() {
		return registrationCenterAddrLine1;
	}

	/**
	 * @param registrationCenterAddrLine1
	 *            the registrationCenterAddrLine1 to set
	 */
	public void setRegistrationCenterAddrLine1(String registrationCenterAddrLine1) {
		this.registrationCenterAddrLine1 = registrationCenterAddrLine1;
	}

	/**
	 * @return the registrationCenterAddrLine2
	 */
	public String getRegistrationCenterAddrLine2() {
		return registrationCenterAddrLine2;
	}

	/**
	 * @param registrationCenterAddrLine2
	 *            the registrationCenterAddrLine2 to set
	 */
	public void setRegistrationCenterAddrLine2(String registrationCenterAddrLine2) {
		this.registrationCenterAddrLine2 = registrationCenterAddrLine2;
	}

	/**
	 * @return the registrationCenterAddrLine3
	 */
	public String getRegistrationCenterAddrLine3() {
		return registrationCenterAddrLine3;
	}

	/**
	 * @param registrationCenterAddrLine3
	 *            the registrationCenterAddrLine3 to set
	 */
	public void setRegistrationCenterAddrLine3(String registrationCenterAddrLine3) {
		this.registrationCenterAddrLine3 = registrationCenterAddrLine3;
	}

	/**
	 * @return the registrationCenterLatitude
	 */
	public String getRegistrationCenterLatitude() {
		return registrationCenterLatitude;
	}

	/**
	 * @param registrationCenterLatitude
	 *            the registrationCenterLatitude to set
	 */
	public void setRegistrationCenterLatitude(String registrationCenterLatitude) {
		this.registrationCenterLatitude = registrationCenterLatitude;
	}

	/**
	 * @return the registrationCenterLongitude
	 */
	public String getRegistrationCenterLongitude() {
		return registrationCenterLongitude;
	}

	/**
	 * @param registrationCenterLongitude
	 *            the registrationCenterLongitude to set
	 */
	public void setRegistrationCenterLongitude(String registrationCenterLongitude) {
		this.registrationCenterLongitude = registrationCenterLongitude;
	}

	/**
	 * @return the registrationCenterLocationCode
	 */
	public String getRegistrationCenterLocationCode() {
		return registrationCenterLocationCode;
	}

	/**
	 * @param registrationCenterLocationCode
	 *            the registrationCenterLocationCode to set
	 */
	public void setRegistrationCenterLocationCode(String registrationCenterLocationCode) {
		this.registrationCenterLocationCode = registrationCenterLocationCode;
	}

	/**
	 * @return the registrationCenterNumberOfStations
	 */
	public int getRegistrationCenterNumberOfStations() {
		return registrationCenterNumberOfStations;
	}

	/**
	 * @param registrationCenterNumberOfStations
	 *            the registrationCenterNumberOfStations to set
	 */
	public void setRegistrationCenterNumberOfStations(int registrationCenterNumberOfStations) {
		this.registrationCenterNumberOfStations = registrationCenterNumberOfStations;
	}

	/**
	 * @return the registrationCenterWorkingHours
	 */
	public String getRegistrationCenterWorkingHours() {
		return registrationCenterWorkingHours;
	}

	/**
	 * @param registrationCenterWorkingHours
	 *            the registrationCenterWorkingHours to set
	 */
	public void setRegistrationCenterWorkingHours(String registrationCenterWorkingHours) {
		this.registrationCenterWorkingHours = registrationCenterWorkingHours;
	}

	/**
	 * @return the registrationCenterContactPhone
	 */
	public String getRegistrationCenterContactPhone() {
		return registrationCenterContactPhone;
	}

	/**
	 * @param registrationCenterContactPhone
	 *            the registrationCenterContactPhone to set
	 */
	public void setRegistrationCenterContactPhone(String registrationCenterContactPhone) {
		this.registrationCenterContactPhone = registrationCenterContactPhone;
	}

	/**
	 * @return the registrationCenterNumberOfKiosks
	 */
	public Integer getRegistrationCenterNumberOfKiosks() {
		return registrationCenterNumberOfKiosks;
	}

	/**
	 * @param registrationCenterNumberOfKiosks
	 *            the registrationCenterNumberOfKiosks to set
	 */
	public void setRegistrationCenterNumberOfKiosks(Integer registrationCenterNumberOfKiosks) {
		this.registrationCenterNumberOfKiosks = registrationCenterNumberOfKiosks;
	}

	/**
	 * @return the registrationCenterPerKioskProcessTime
	 */
	public Time getRegistrationCenterPerKioskProcessTime() {
		return registrationCenterPerKioskProcessTime;
	}

	/**
	 * @param registrationCenterPerKioskProcessTime
	 *            the registrationCenterPerKioskProcessTime to set
	 */
	public void setRegistrationCenterPerKioskProcessTime(Time registrationCenterPerKioskProcessTime) {
		this.registrationCenterPerKioskProcessTime = registrationCenterPerKioskProcessTime;
	}

	/**
	 * @return the registrationCenterProcessStartTime
	 */
	public Time getRegistrationCenterProcessStartTime() {
		return registrationCenterProcessStartTime;
	}

	/**
	 * @param registrationCenterProcessStartTime
	 *            the registrationCenterProcessStartTime to set
	 */
	public void setRegistrationCenterProcessStartTime(Time registrationCenterProcessStartTime) {
		this.registrationCenterProcessStartTime = registrationCenterProcessStartTime;
	}

	/**
	 * @return the registrationCenterProcessEndTime
	 */
	public Time getRegistrationCenterProcessEndTime() {
		return registrationCenterProcessEndTime;
	}

	/**
	 * @param registrationCenterProcessEndTime
	 *            the registrationCenterProcessEndTime to set
	 */
	public void setRegistrationCenterProcessEndTime(Time registrationCenterProcessEndTime) {
		this.registrationCenterProcessEndTime = registrationCenterProcessEndTime;
	}

	/**
	 * @return the registrationCenterHolidayLocCode
	 */
	public String getRegistrationCenterHolidayLocCode() {
		return registrationCenterHolidayLocCode;
	}

	/**
	 * @param registrationCenterHolidayLocCode
	 *            the registrationCenterHolidayLocCode to set
	 */
	public void setRegistrationCenterHolidayLocCode(String registrationCenterHolidayLocCode) {
		this.registrationCenterHolidayLocCode = registrationCenterHolidayLocCode;
	}

}
