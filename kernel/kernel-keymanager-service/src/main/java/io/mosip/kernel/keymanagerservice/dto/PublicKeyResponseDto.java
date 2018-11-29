/**
 * 
 */
package io.mosip.kernel.keymanagerservice.dto;

import java.time.LocalDateTime;

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
public class PublicKeyResponseDto {

	private byte[] publicKey;
	
	private LocalDateTime keyExpiryTime;
	
	private LocalDateTime keyGenerationTime;

}
