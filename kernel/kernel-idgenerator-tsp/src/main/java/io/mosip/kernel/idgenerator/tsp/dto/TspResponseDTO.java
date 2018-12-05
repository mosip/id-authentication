package io.mosip.kernel.idgenerator.tsp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The DTO for TSPID.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TspResponseDTO {

	/**
	 * The TSPID generated.
	 */
	private int tspId;

}
