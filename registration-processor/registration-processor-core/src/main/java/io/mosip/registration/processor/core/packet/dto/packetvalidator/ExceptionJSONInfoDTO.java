package io.mosip.registration.processor.core.packet.dto.packetvalidator;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ExceptionJSONInfoDTO implements Serializable {

	private static final long serialVersionUID = -1502624259085162357L;
	/**
	 * Error Code
	 */
	private String errorCode;

	/**
	 * Error Message
	 */
	private String message;

}
