package io.mosip.registration.processor.request.handler.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
	
/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Data

/**
 * Instantiates a new registration center dto.
 *
 * @param id the id
 * @param name the name
 * @param centerTypeCode the center type code
 * @param addressLine1 the address line 1
 * @param addressLine2 the address line 2
 * @param addressLine3 the address line 3
 * @param latitude the latitude
 * @param longitude the longitude
 * @param locationCode the location code
 * @param holidayLocationCode the holiday location code
 * @param contactPhone the contact phone
 * @param numberOfStations the number of stations
 * @param workingHours the working hours
 * @param languageCode the language code
 * @param numberOfKiosks the number of kiosks
 * @param perKioskProcessTime the per kiosk process time
 * @param centerStartTime the center start time
 * @param centerEndTime the center end time
 * @param timeZone the time zone
 * @param contactPerson the contact person
 * @param lunchStartTime the lunch start time
 * @param lunchEndTime the lunch end time
 * @param isActive the is active
 */
@AllArgsConstructor

/**
 * Instantiates a new registration center dto.
 */
@NoArgsConstructor
public class RegistrationCenterDto {

	/** The id. */
	private String id;

	private String name;

	private String centerTypeCode;

	private String addressLine1;

	private String addressLine2;

	private String addressLine3;

	private String latitude;

	private String longitude;

	private String locationCode;

	private String holidayLocationCode;

	private String contactPhone;

	private String workingHours;

	private String langCode;

	private Short numberOfKiosks;

	private String perKioskProcessTime;

	private String centerStartTime;

	private String centerEndTime;

	private String timeZone;

	private String contactPerson;

	private String lunchStartTime;

	private String lunchEndTime;

	private Boolean isActive;
	
	private String zoneCode;
}
