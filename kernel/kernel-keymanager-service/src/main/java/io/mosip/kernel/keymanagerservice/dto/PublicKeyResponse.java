/**
 * 
 */
package io.mosip.kernel.keymanagerservice.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class PublicKeyResponse<T> {
	
	@JsonIgnore
	private String alias;

	private T publicKey;

	private LocalDateTime keyGenerationTime;

	private LocalDateTime keyExpiryTime;

}
