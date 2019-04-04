package io.mosip.kernel.tokenidgenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response dto for vid generator
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenIDResponseDto {

	/**
	 * The tokenid
	 */
	private String tokenID;

}
