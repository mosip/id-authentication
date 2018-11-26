package io.mosip.kernel.masterdata.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dharmesh Khandelwal
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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

	private String languageCode;
	
	private Short numberOfKiosks;
	
	private LocalTime perKioskProcessTime;
	
	private LocalTime processStartTime;
	
	private LocalTime processEndTime;
	
	private Boolean isActive;

}
