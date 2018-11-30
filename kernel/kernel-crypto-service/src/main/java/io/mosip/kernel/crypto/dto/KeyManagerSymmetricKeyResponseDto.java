/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.dto;

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
public class KeyManagerSymmetricKeyResponseDto {
	
	private String symmetricKey;
}