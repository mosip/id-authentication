package io.mosip.registration.dto.mastersync;

/**
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */

public class HolidayDto extends MasterSyncBaseDto {

	private int holidayId;
	
	private String holidayDate;
	
	private String holidayDay;
	
	private String holidayMonth;

	private String holidayYear;
	
	private String holidayName;
	
	private String langCode;

	private String locationCode;

	private Boolean isActive;

	/**
	 * @return the holidayId
	 */
	public int getHolidayId() {
		return holidayId;
	}

	/**
	 * @param holidayId the holidayId to set
	 */
	public void setHolidayId(int holidayId) {
		this.holidayId = holidayId;
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

	/**
	 * @return the locationCode
	 */
	public String getLocationCode() {
		return locationCode;
	}

	/**
	 * @param locationCode the locationCode to set
	 */
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	/**
	 * @return the holidayDate
	 */
	public String getHolidayDate() {
		return holidayDate;
	}

	/**
	 * @param holidayDate the holidayDate to set
	 */
	public void setHolidayDate(String holidayDate) {
		this.holidayDate = holidayDate;
	}

	/**
	 * @return the holidayDay
	 */
	public String getHolidayDay() {
		return holidayDay;
	}

	/**
	 * @param holidayDay the holidayDay to set
	 */
	public void setHolidayDay(String holidayDay) {
		this.holidayDay = holidayDay;
	}

	/**
	 * @return the holidayMonth
	 */
	public String getHolidayMonth() {
		return holidayMonth;
	}

	/**
	 * @param holidayMonth the holidayMonth to set
	 */
	public void setHolidayMonth(String holidayMonth) {
		this.holidayMonth = holidayMonth;
	}

	/**
	 * @return the holidayYear
	 */
	public String getHolidayYear() {
		return holidayYear;
	}

	/**
	 * @param holidayYear the holidayYear to set
	 */
	public void setHolidayYear(String holidayYear) {
		this.holidayYear = holidayYear;
	}

	/**
	 * @return the holidayName
	 */
	public String getHolidayName() {
		return holidayName;
	}

	/**
	 * @param holidayName the holidayName to set
	 */
	public void setHolidayName(String holidayName) {
		this.holidayName = holidayName;
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
