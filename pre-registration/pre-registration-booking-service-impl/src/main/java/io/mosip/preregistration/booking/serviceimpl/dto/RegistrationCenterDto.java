package io.mosip.preregistration.booking.serviceimpl.dto;
import java.time.LocalTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@ToString
public class RegistrationCenterDto {

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

	private Short numberOfStations;

	private String workingHours;

	private String langCode;

	private Short numberOfKiosks;

	private LocalTime perKioskProcessTime;

	private LocalTime centerStartTime;

	private LocalTime centerEndTime;

	private String timeZone;

	private String contactPerson;

	private LocalTime lunchStartTime;

	private LocalTime lunchEndTime;

	private Boolean isActive;

}
