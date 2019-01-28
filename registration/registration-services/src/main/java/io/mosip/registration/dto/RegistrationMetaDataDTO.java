package io.mosip.registration.dto;

/**
 * This class contains the meta-information of the Registration
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
public class RegistrationMetaDataDTO extends BaseDTO {
	private double geoLatitudeLoc;
	private double geoLongitudeLoc;
	// New , update , correction, lost UIN
	private String applicationType;
	// Document Based or Introducer Based
	private String registrationCategory;
	private String machineId;
	private String centerId;
	private String previousRID;
	private String uin;

	/**
	 * @return the geoLatitudeLoc
	 */
	public double getGeoLatitudeLoc() {
		return geoLatitudeLoc;
	}

	/**
	 * @param geoLatitudeLoc
	 *            the geoLatitudeLoc to set
	 */
	public void setGeoLatitudeLoc(double geoLatitudeLoc) {
		this.geoLatitudeLoc = geoLatitudeLoc;
	}

	/**
	 * @return the geoLongitudeLoc
	 */
	public double getGeoLongitudeLoc() {
		return geoLongitudeLoc;
	}

	/**
	 * @param geoLongitudeLoc
	 *            the geoLongitudeLoc to set
	 */
	public void setGeoLongitudeLoc(double geoLongitudeLoc) {
		this.geoLongitudeLoc = geoLongitudeLoc;
	}

	/**
	 * @return the applicationType
	 */
	public String getApplicationType() {
		return applicationType;
	}

	/**
	 * @param applicationType
	 *            the applicationType to set
	 */
	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}

	/**
	 * @return the registrationCategory
	 */
	public String getRegistrationCategory() {
		return registrationCategory;
	}

	/**
	 * @param registrationCategory
	 *            the registrationCategory to set
	 */
	public void setRegistrationCategory(String registrationCategory) {
		this.registrationCategory = registrationCategory;
	}

	/**
	 * @return the machineId
	 */
	public String getMachineId() {
		return machineId;
	}

	/**
	 * @param machineId
	 *            the machineId to set
	 */
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}

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
	 * @return the previousRID
	 */
	public String getPreviousRID() {
		return previousRID;
	}

	/**
	 * @param previousRID
	 *            the previousRID to set
	 */
	public void setPreviousRID(String previousRID) {
		this.previousRID = previousRID;
	}

	/**
	 * @return the uin
	 */
	public String getUin() {
		return uin;
	}

	/**
	 * @param uin
	 *            the uin to set
	 */
	public void setUin(String uin) {
		this.uin = uin;
	}

}
