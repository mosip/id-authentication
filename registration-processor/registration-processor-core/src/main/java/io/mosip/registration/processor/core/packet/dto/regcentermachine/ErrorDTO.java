/**
 * 
 */
package io.mosip.registration.processor.core.packet.dto.regcentermachine;

import lombok.Data;

/**
 * @author M1022006
 *
 */
@Data
public class ErrorDTO {

	private String errorCode;

	private String errorMessage;
}
