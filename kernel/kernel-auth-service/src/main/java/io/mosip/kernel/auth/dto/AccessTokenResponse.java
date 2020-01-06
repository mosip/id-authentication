package io.mosip.kernel.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccessTokenResponse {
	private String access_token;
	private String expires_in;
	private String refresh_expires_in;
	private String refresh_token;
	private String token_type;
	private String session_state;
	private String scope;
}
