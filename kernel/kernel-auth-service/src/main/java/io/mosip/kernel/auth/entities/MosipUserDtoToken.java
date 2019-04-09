package io.mosip.kernel.auth.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sabbu Uday Kumar
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MosipUserDtoToken {
	private MosipUserDto mosipUserDto;
	private String token;
	private String refreshToken;
	private long expTime;
	private String message;
	private String status;
}
