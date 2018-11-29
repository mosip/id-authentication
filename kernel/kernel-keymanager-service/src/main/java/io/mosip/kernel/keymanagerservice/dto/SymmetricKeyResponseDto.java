/**
 * 
 */
package io.mosip.kernel.keymanagerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SymmetricKeyResponseDto {

	private byte[] symmetricKey;

}
