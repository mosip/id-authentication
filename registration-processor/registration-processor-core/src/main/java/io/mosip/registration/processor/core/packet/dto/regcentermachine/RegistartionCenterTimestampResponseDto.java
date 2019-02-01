/**
 * 
 */
package io.mosip.registration.processor.core.packet.dto.regcentermachine;

import java.util.List;

import lombok.Data;

/**
 * The Class RegistartionCenterTimestampResponseDto.
 *
 * @author Ranjitha Siddegowda
 */
@Data
public class RegistartionCenterTimestampResponseDto {

	/** The timestamp. */
	private String timestamp;
	
	/** The status. */
	private String status;
	
	/** The errors. */
	private List<ErrorDTO> errors;

	@Override
	public String toString() {
		return "RegistartionCenterTimestampResponseDto [timestamp=" + timestamp + ", status=" + status + ", errors="
				+ errors + "]";
	}
	
}
