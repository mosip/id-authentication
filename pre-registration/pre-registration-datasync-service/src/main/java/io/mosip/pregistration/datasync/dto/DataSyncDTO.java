package io.mosip.pregistration.datasync.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author M1043226
 *
 */
@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder(value={ "id", "ver", "reqTime", "request"})
public class DataSyncDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("id")
	private String id;
	
	@JsonProperty("ver")
	private String ver;
	
	@JsonProperty("reqTime")
	private Date reqTime;
	
	/**
	 * object to accept json
	 */
	@JsonProperty("request")
	private DataSyncRequestDTO dataSyncRequestDto;
}
