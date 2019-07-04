package io.mosip.registration.processor.core.packet.dto.packetvalidator;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * 
 * @author Girish Yarru
 *
 * @param <T>
 */

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MainResponseDTO<T> implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3384945682672832638L;
	/**
	 * Id
	 */
	@ApiModelProperty(value = "request id", position = 1)
	private String id;
	/**
	 * version
	 */
	@ApiModelProperty(value = "request version", position = 2)
	private String version;

	@ApiModelProperty(value = "Response Time", position = 3)
	private String responsetime;

	@ApiModelProperty(value = "Response", position = 4)
	private T response;

	/** The error details. */
	@ApiModelProperty(value = "Error Details", position = 5)
	private List<ExceptionJSONInfoDTO> errors;
}
