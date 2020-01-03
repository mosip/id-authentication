package io.mosip.authentication.core.indauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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
	
	@JsonProperty("dp")
	private String deviceProvider;
	
	@JsonProperty("dpId")
	private String deviceProviderId;
	
	private String dateTime;
}
