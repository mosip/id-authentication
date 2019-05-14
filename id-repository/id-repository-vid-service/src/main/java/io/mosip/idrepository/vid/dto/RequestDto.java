package io.mosip.idrepository.vid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * This Class will provide values of vidStatus,vidType and uin.
 * 
 * @author Prem kumar
 *
 */
@Data
public class RequestDto {

	/** The Value to hold vidStatus */
	private String vidStatus;

	/** The Value to hold vidType */
	private String vidType;

	/** The Value to hold uin */
	@JsonProperty("UIN")
	private String uin;

}
