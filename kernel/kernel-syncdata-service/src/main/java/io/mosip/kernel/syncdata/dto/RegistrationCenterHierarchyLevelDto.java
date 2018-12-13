package io.mosip.kernel.syncdata.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationCenterHierarchyLevelDto extends BaseDto{
	private String id;
	
	private String name;
	
	private String addressLine1;

	private String addressLine2;

	private String addressLine3;

	private String latitude;

	private String longitude;
	
	private String centerTypeCode;
	
	private Boolean isActive;
	
	private String workingHours;
	
	private String contactPhone;
	
	private Short numberOfKiosks;
	
	private LocalTime perKioskProcessTime;

	private LocalTime centerStartTime;

	private LocalTime centerEndTime;
	
	private String timeZone;

	private String contactPerson;

	private LocalTime lunchStartTime;

	private LocalTime lunchEndTime;

	
	


}