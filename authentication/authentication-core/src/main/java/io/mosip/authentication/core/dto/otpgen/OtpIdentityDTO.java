package io.mosip.authentication.core.dto.otpgen;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * This class holds the values of uin and vid
 * 
 * @author Prem Kumar
 *
 */@Data
public class OtpIdentityDTO {
	/** The value UIN */
	@JsonProperty("UIN")
	private String uin;
	
	/** The value VID */
	@JsonProperty("VID")
	private String vid;
}
