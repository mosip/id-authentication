package io.mosip.registration.dto.mastersync;

/**
 * @author Sreekar Chukka
 * @since 1.0.0
 */

public class LocationCodeDto extends MasterSyncBaseDto {

	private String code;
	private String parentLocCode;
	private Boolean isActive;
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the parentLocCode
	 */
	public String getParentLocCode() {
		return parentLocCode;
	}
	/**
	 * @param parentLocCode the parentLocCode to set
	 */
	public void setParentLocCode(String parentLocCode) {
		this.parentLocCode = parentLocCode;
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
