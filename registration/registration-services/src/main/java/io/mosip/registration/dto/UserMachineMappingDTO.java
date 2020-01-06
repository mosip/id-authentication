package io.mosip.registration.dto;

import lombok.Data;

/**
 * DTO class for User Machine Mapping details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Data
public class UserMachineMappingDTO {	
	private String userID;
	private String centreID;
	private String machineID;
	private String langCode;
	private boolean isActive;
	
	private MachineMasterDTO machineMaster;
}
