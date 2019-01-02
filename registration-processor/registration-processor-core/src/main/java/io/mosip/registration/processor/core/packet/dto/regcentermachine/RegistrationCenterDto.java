package io.mosip.registration.processor.core.packet.dto.regcentermachine;

import java.time.LocalTime;

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

	/** The name. */
	private String name;

	/** The center type code. */
	private String centerTypeCode;

	/** The address line 1. */
	private String addressLine1;

	/** The address line 2. */
	private String addressLine2;

	/** The address line 3. */
	private String addressLine3;

	/** The latitude. */
	private String latitude;

	/** The longitude. */
	private String longitude;

	/** The location code. */
	private String locationCode;

	/** The holiday location code. */
	private String holidayLocationCode;

	/** The contact phone. */
	private String contactPhone;

	/** The number of stations. */
	private Short numberOfStations;

	/** The working hours. */
	private String workingHours;

	/** The language code. */
	private String languageCode;

	/** The number of kiosks. */
	private Short numberOfKiosks;

	/** The per kiosk process time. */
	private LocalTime perKioskProcessTime;

	/** The center start time. */
	private LocalTime centerStartTime;

	/** The center end time. */
	private LocalTime centerEndTime;

	/** The time zone. */
	private String timeZone;

	/** The contact person. */
	private String contactPerson;

	/** The lunch start time. */
	private LocalTime lunchStartTime;

	/** The lunch end time. */
	private LocalTime lunchEndTime;

	/** The is active. */
	private Boolean isActive;

}
