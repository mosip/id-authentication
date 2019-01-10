package io.mosip.registration.entity.mastersync;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

//github.com/mosip/mosip.git

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.mosip.registration.entity.mastersync.id.HolidayID;

/**
 * 
 * @author Sreekar chukka
 * @since 1.0.0
 */

@Entity
@Table(name = "loc_holiday", schema = "reg")
public class MasterHoliday extends MasterSyncBaseEntity implements Serializable {

	private static final long serialVersionUID = 1329042436883315822L;

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "locationCode", column = @Column(name = "location_code")),
			@AttributeOverride(name = "holidayDate", column = @Column(name = "holiday_date")),
			@AttributeOverride(name = "holidayName", column = @Column(name = "holiday_name")),
			@AttributeOverride(name = "langCode", column = @Column(name = "lang_code")) })

	private HolidayID hId;

	@Column(name = "id")
	private int holidayId;

	@Column(name = "holiday_desc")
	private String holidayDesc;

	/**
	 * @return the hId
	 */
	public HolidayID gethId() {
		return hId;
	}

	/**
	 * @param hId the hId to set
	 */
	public void sethId(HolidayID hId) {
		this.hId = hId;
	}

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
	 * @return the holidayDesc
	 */
	public String getHolidayDesc() {
		return holidayDesc;
	}

	/**
	 * @param holidayDesc the holidayDesc to set
	 */
	public void setHolidayDesc(String holidayDesc) {
		this.holidayDesc = holidayDesc;
	}

}
