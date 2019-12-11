package io.mosip.authentication.core.indauth.dto;

import lombok.Data;

/**
 * @author Manoj SP
 *
 */
@Data
public class DigitalId {
	
	private String serialNo;
	
	private String make;
	
	private String model;
	
	private String type;
	
	private String subType;
	
	private String deviceProvider;
	
	private String deviceProviderId;
	
	private String dateTime;
}
