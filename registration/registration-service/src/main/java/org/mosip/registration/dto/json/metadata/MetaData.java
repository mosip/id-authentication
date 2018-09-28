package org.mosip.registration.dto.json.metadata;

public class MetaData {
	private GeoLocation geoLocation;
	private String applicationType;
	private String applicationCategory;
	private String preRegistrationId;
	private String registrationId;

	/**
	 * @return the geoLocation
	 */
	public GeoLocation getGeoLocation() {
		return geoLocation;
	}

	/**
	 * @param geoLocation
	 *            the geoLocation to set
	 */
	public void setGeoLocation(GeoLocation geoLocation) {
		this.geoLocation = geoLocation;
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
	 * @return the applicationCategory
	 */
	public String getApplicationCategory() {
		return applicationCategory;
	}

	/**
	 * @param applicationCategory
	 *            the applicationCategory to set
	 */
	public void setApplicationCategory(String applicationCategory) {
		this.applicationCategory = applicationCategory;
	}

	/**
	 * @return the preRegistrationId
	 */
	public String getPreRegistrationId() {
		return preRegistrationId;
	}

	/**
	 * @param preRegistrationId
	 *            the preRegistrationId to set
	 */
	public void setPreRegistrationId(String preRegistrationId) {
		this.preRegistrationId = preRegistrationId;
	}

	/**
	 * @return the registrationId
	 */
	public String getRegistrationId() {
		return registrationId;
	}

	/**
	 * @param registrationId
	 *            the registrationId to set
	 */
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}
}
