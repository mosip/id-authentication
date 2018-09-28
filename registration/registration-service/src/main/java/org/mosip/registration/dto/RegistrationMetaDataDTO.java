package org.mosip.registration.dto;

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
	// Infant or Child, Regular
	private String applicationCategory;

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

}
