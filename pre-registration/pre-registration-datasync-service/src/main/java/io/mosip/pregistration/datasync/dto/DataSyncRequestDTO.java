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
public class DataSyncRequestDTO implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Registration-client-Id. */
	@JsonProperty("registartion-client-id")
	private String regClientId;
	
	/** The from-date. */
	@JsonProperty("from-date")
	private Timestamp fromDate;
	
	/** The To-date. */
	@JsonProperty("to-date")
	private Timestamp toDate;
	
	/** The UserId. */
	@JsonProperty("user-id")
	private String userId;
}
