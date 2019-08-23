package io.mosip.registration.processor.request.handler.service.dto;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;

/**
 * The Class SuccessResponseDTO.
 */
@Data
public class SuccessResponseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7134234134914580248L;

	/** The code. */
	private String code;

	/** The message. */
	private String message;

	/** The other attributes. */
	private Map<String, Object> otherAttributes;

	/** The info type. */
	private String infoType;

}
