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
public class DataSyncRequestDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Registration-client-Id. */
	@JsonProperty("registrationCenterId")
	@ApiModelProperty(value = "Registration client id", position = 1)
	private String registrationCenterId;

	/** The from-date. */
	@JsonProperty("fromDate")
	@ApiModelProperty(value = "From date", position = 2)
	private String fromDate;

	/** The To-date. */
	@JsonProperty("toDate")
	@ApiModelProperty(value = "To date", position = 3)
	private String toDate;

}
