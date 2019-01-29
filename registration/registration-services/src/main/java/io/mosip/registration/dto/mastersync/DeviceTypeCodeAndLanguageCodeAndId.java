package io.mosip.registration.dto.mastersync;
/**
 * Response class for Device specification save 
 * 
 * @author Sreekar Chukka
 * @version 1.0.0
 */


public class DeviceTypeCodeAndLanguageCodeAndId extends MasterSyncBaseDto{
	
	private String id;
	private String deviceTypeCode;
	private String langCode;
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
	 * @return the deviceTypeCode
	 */
	public String getDeviceTypeCode() {
		return deviceTypeCode;
	}
	/**
	 * @param deviceTypeCode the deviceTypeCode to set
	 */
	public void setDeviceTypeCode(String deviceTypeCode) {
		this.deviceTypeCode = deviceTypeCode;
	}
	/**
	 * @return the langCode
	 */
	public String getLangCode() {
		return langCode;
	}
	/**
	 * @param langCode the langCode to set
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	
	
	

}
