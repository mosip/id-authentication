package io.mosip.registration.dto;

import lombok.Data;

/**
 * DTO class for Machine Master details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Data
public class MachineMasterDTO {	
	private String name;
	private String serialNum;
	private String macAddress;
}
