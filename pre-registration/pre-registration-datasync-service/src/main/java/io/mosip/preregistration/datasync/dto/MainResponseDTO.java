package io.mosip.preregistration.datasync.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MainResponseDTO<T> implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3384945682672832638L;

	/** The error details. */
	@ApiModelProperty(value = "Error Details", position = 1)
	private ExceptionJSONInfoDTO err;

	@ApiModelProperty(value = "Response Status", position = 2)
	private boolean status;

	@ApiModelProperty(value = "Response Time", position = 3)
	private String resTime;

	@ApiModelProperty(value = "Response", position = 4)
	private T response;

}
