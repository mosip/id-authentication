package io.mosip.kernel.auth.adapter.model;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/***********************************************************************************************************************
 * AUTH_TOKEN USED TO ACCESS TOKEN DETAILS
 *
 * @author Sabbu Uday Kumar
 * @since 1.0.0
 **********************************************************************************************************************/

public class AuthToken extends UsernamePasswordAuthenticationToken {

	private static final long serialVersionUID = 4068560701182593212L;
	
	private String token;

	public AuthToken(String token) {
		super(null, null);
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}