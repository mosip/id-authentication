package io.mosip.resident.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Instantiates a new error DTO.
 *
 * @param errorCode
 *            the errorcode
 * @param errorMessage
 *            the message
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO implements Serializable {

	private static final long serialVersionUID = 2452990684776944908L;

	/** The errorcode. */
	private String errorCode;

	/** The message. */
	private String errorMessage;
}
