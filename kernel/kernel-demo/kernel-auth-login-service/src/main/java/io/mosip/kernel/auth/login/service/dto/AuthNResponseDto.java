/**
 * 
 */
package io.mosip.kernel.auth.login.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ramadurai Pandian
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthNResponseDto {
	
	private String token;
	
	private String message;
	
	private long expiryTime;
	
	private String userId;

}
