package io.mosip.kernel.responsesignature.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoManagerResponseDto {
	
	/**
	 * encrypted data
	 */
	private String data;
}
