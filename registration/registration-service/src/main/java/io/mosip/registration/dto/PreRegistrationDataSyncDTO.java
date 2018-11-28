package io.mosip.registration.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
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
public class PreRegistrationDataSyncDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String ver;
	private Timestamp reqTime;
	
	/**
	 * object to accept json
	 */
	@JsonProperty("request")
	private PreRegistrationDataSyncRequestDTO dataSyncRequestDto;
}
