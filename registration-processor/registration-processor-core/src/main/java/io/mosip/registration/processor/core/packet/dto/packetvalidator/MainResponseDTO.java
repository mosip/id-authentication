package io.mosip.registration.processor.core.packet.dto.packetvalidator;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 
 * @author Girish Yarru
 *
 * @param <T>
 */

@Data
@ToString
public class MainResponseDTO<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5305832911895831989L;

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
