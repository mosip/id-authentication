/**
 * 
 */
package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * @author Sowmya
 *
 */
@Data
public class ErrorDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 6381068666467968031L;

	private String errorCode;

	private String errorMessage;
}
