package io.mosip.kernel.keymanagerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for symmetric Key
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SymmetricKeyResponseDto {

	/**
	 * The string symmetric Key
	 */
	private String symmetricKey;

}
