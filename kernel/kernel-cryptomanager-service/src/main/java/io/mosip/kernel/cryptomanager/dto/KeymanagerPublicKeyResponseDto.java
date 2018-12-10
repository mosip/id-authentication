/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptomanager.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeymanagerPublicKeyResponseDto {
    
	private String publicKey;

	private LocalDateTime issuedAt;

	private LocalDateTime expiryAt;

}
