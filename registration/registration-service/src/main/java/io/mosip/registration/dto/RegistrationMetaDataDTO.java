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
	public double getGeoLatitudeLoc() {
		return geoLatitudeLoc;
	}
	public void setGeoLatitudeLoc(double geoLatitudeLoc) {
		this.geoLatitudeLoc = geoLatitudeLoc;
	}
	public double getGeoLongitudeLoc() {
		return geoLongitudeLoc;
	}
	public void setGeoLongitudeLoc(double geoLongitudeLoc) {
		this.geoLongitudeLoc = geoLongitudeLoc;
	}
	public String getApplicationType() {
		return applicationType;
	}
	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}
	public String getRegistrationCategory() {
		return registrationCategory;
	}
	public void setRegistrationCategory(String registrationCategory) {
		this.registrationCategory = registrationCategory;
	}
	
}
