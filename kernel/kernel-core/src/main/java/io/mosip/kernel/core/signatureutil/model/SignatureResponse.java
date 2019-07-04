package io.mosip.kernel.core.signatureutil.model;

import java.time.LocalDateTime;

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
public class SignatureResponse {
	
	/**
	 * encrypted data
	 */
	private String data;
	
	/**
	 * response time.
	 */
	private LocalDateTime timestamp;
}
