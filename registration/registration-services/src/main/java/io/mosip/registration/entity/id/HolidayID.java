package io.mosip.registration.entity.id;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import io.mosip.registration.entity.Holiday;
import lombok.Data;

/**
 * Composite key for {@link Holiday}
 *
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Embeddable
@Data
public class HolidayID implements Serializable {

	private static final long serialVersionUID = -1631873932622755759L;

	@Column(name = "location_code")
	private String locationCode;

	@Column(name = "holiday_date")
	private LocalDate holidayDate;

	@Column(name = "holiday_name")
	private String holidayName;

	@Column(name = "lang_code")
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
	public LocalDate getHolidayDate() {
		return holidayDate;
	}

	/**
	 * @param holidayDate the holidayDate to set
	 */
	public void setHolidayDate(LocalDate holidayDate) {
		this.holidayDate = holidayDate;
	}

	

}
