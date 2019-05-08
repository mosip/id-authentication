package io.mosip.idrepository.vid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 
 * @author Prem Kumar
 *
 */
@Data
public class ResponseDto {

	/** The Value Of UIN in Decrypted value */
	@JsonProperty("UIN")
	private String uin;

	@JsonProperty("VID")
	private String vid;

	private String vidStatus;

}
