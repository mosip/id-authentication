/**
 * 
 */
package io.mosip.kernel.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ramadurai Pandian
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthToken {

	private String userId;
	private String accessToken;
	private long expirationTime;
	private String refreshToken;
}
