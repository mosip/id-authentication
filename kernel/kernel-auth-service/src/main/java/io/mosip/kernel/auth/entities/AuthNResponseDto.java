/**
 * 
 */
package io.mosip.kernel.auth.entities;

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

	private String refreshToken;

	private long expiryTime;

	private String userId;

	private String status;

}
