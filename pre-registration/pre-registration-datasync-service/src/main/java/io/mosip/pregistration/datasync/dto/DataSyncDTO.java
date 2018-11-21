package io.mosip.pregistration.datasync.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

/**
 * @author M1043226
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DataSyncDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String ver;
	private Timestamp reqTime;
	
	@JsonProperty("request")
	private DataSyncRequestDTO dataSyncRequestDto;
}
