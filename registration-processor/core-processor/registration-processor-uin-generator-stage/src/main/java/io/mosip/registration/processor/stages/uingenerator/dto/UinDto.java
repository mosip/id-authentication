/**
 * 
 */
package io.mosip.registration.processor.stages.uingenerator.dto;

import java.util.List;

import io.mosip.registration.processor.core.packet.dto.regcentermachine.ErrorDTO;
import lombok.Data;
import lombok.ToString;

/**
 * @author Ranjitha Siddegowda
 *
 */
@Data
@ToString
public class UinDto extends UinResponseDto {
	
	private String status;
	
	private List<ErrorDTO> errors;

}
