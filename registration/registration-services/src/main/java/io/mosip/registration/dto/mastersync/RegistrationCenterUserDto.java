package io.mosip.registration.dto.mastersync;

/**
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
public class RegistrationCenterUserDto extends MasterSyncBaseDto {

	private String regCenterId;

	private String userId;

	private Boolean isActive;

	private String langCode;

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

	/**
	 * @return the regCenterId
	 */
	public String getRegCenterId() {
		return regCenterId;
	}

	/**
	 * @param regCenterId the regCenterId to set
	 */
	public void setRegCenterId(String regCenterId) {
		this.regCenterId = regCenterId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
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
