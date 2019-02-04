package io.mosip.preregistration.datasync.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author M1043226
 *
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DataSyncRequestDTO implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Registration-client-Id. */
	@JsonProperty("registration-client-id")
	@ApiModelProperty(value = "Registration client id", position = 1)
	private String regClientId;
	
	/** The from-date. */
	@JsonProperty("from-date")
	@ApiModelProperty(value = "From date", position = 2)
	private String fromDate;
	
	/** The To-date. */
	@JsonProperty("to-date")
	@ApiModelProperty(value = "To date", position = 3)
	private String toDate;
	
	/** The UserId. */
	@JsonProperty("user-id")
	@ApiModelProperty(value = "Registration client user Id", position = 4)
	private String userId;
}
