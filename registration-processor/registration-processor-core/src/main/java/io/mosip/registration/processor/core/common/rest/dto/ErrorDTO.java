package io.mosip.registration.processor.core.common.rest.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Instantiates a new error DTO.
 *
 * @param errorcode the errorcode
 * @param message the message
 * 
 * @author Rishabh Keshari
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO implements Serializable {

	private static final long serialVersionUID = 2452990684776944908L;

	/** The errorcode. */
	private String errorCode;
	
	/** The message. */
	private String message;
}
