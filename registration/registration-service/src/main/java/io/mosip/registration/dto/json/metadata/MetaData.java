package io.mosip.registration.dto.json.metadata;

/**
 * This class is to capture the json parsing meta data 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
public class MetaData {
	private GeoLocation geoLocation;
	private String applicationType;
	private String registrationCategory;
	private String preRegistrationId;
	private String registrationId;
	private String registrationIdHash;
	public GeoLocation getGeoLocation() {
		return geoLocation;
	}
	public void setGeoLocation(GeoLocation geoLocation) {
		this.geoLocation = geoLocation;
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
	public String getPreRegistrationId() {
		return preRegistrationId;
	}
	public void setPreRegistrationId(String preRegistrationId) {
		this.preRegistrationId = preRegistrationId;
	}
	public String getRegistrationId() {
		return registrationId;
	}
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}
	public String getRegistrationIdHash() {
		return registrationIdHash;
	}
	public void setRegistrationIdHash(String registrationIdHash) {
		this.registrationIdHash = registrationIdHash;
	}
	
}
