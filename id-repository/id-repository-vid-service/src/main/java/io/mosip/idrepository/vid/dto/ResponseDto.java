package io.mosip.idrepository.vid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * This class will provide values to hold uin,vid and vidStatus
 * 
 * @author Prem Kumar
 *
 */
@Data
public class ResponseDto {

	/** The Value Of UIN in Decrypted value */
	@JsonProperty("UIN")
	private String uin;

	/** The Value to hold vid */
	@JsonProperty("VID")
	private String vid;

	/** The Value to hold vidStatus */
	private String vidStatus;

}
