package io.mosip.preregistration.datasync.dto;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO
 * 
 * @author M1037717
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class MainListResponseDTO<T> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;

	/** The error details. */
	@ApiModelProperty(value = "Error Details", position = 1)
	private ExceptionJSONInfoDTO err;

	@ApiModelProperty(value = "Response Status", position = 2)
	private boolean status;

	@ApiModelProperty(value = "Response Time", position = 3)
	private String resTime;

	@ApiModelProperty(value = "List of Response", position = 4)
	private List<T> response;

}