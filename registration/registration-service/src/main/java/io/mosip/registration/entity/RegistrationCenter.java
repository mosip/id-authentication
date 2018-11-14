package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * RegistrationCenter entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "registration_center")
public class RegistrationCenter extends RegistrationCommonFields {
	
	@EmbeddedId
	private RegistrationCenterId registrationCenterId;

	@Column(name = "name", length = 128, nullable = false, updatable = false)
	private String centerName;
	@Column(name = "cntr_typ_code", length = 64, nullable = true, updatable = false)
	private String cntrTypCode;
	@Column(name = "addr_line1", length = 256, nullable = true, updatable = false)
	private String addrLine1;
	@Column(name = "addr_line2", length = 256, nullable = true, updatable = false)
	private String addrLine2;
	@Column(name = "addr_line3", length = 256, nullable = true, updatable = false)
	private String addrLine3;
	@Column(name = "latitude", length = 32, nullable = true, updatable = false)
	private String latitude;
	@Column(name = "longitude", length = 32, nullable = true, updatable = false)
	private String longitude;
	@Column(name = "location_Code", length = 32, nullable = false, updatable = false)
	private String locationCode;
	@Column(name = "number_of_stations", nullable = true, updatable = false)
	private int numberOfStations;
	@Column(name = "working_hours", length = 32, nullable = true, updatable = false)
	private String workingHours;
	@Column(name = "is_deleted", nullable = true, updatable = false)
	@Type(type = "true_false")
	private boolean isDeleted;
	@Column(name = "del_dtimes", nullable = true, updatable = false)
	private Timestamp delDtimes;

	/**
	 * @return the registrationCenterId
	 */
	public RegistrationCenterId getRegistrationCenterId() {
		return registrationCenterId;
	}

	/**
	 * @param registrationCenterId
	 *            the registrationCenterId to set
	 */
	public void setRegistrationCenterId(RegistrationCenterId registrationCenterId) {
		this.registrationCenterId = registrationCenterId;
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
	 * @return the numberOfStations
	 */
	public int getNumberOfStations() {
		return numberOfStations;
	}

	/**
	 * @param numberOfStations
	 *            the numberOfStations to set
	 */
	public void setNumberOfStations(int numberOfStations) {
		this.numberOfStations = numberOfStations;
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
	 * @return the isDeleted
	 */
	public boolean isDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted
	 *            the isDeleted to set
	 */
	public void setDeleted(boolean isDeleted) {
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
