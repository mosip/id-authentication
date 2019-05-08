package io.mosip.idrepository.vid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 
 * @author Prem kumar
 *
 */
@Data
public class RequestDto {

	private String vidStatus;

	private String vidType;

	@JsonProperty("UIN")
	private String uin;

}
