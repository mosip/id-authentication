package io.mosip.kernel.masterdata.entity;



import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class AuthenticationToken extends UsernamePasswordAuthenticationToken {
	private static final long serialVersionUID = 5222731796705775254L;





	public AuthenticationToken(Object principal, Object credentials) {
		super(principal, credentials);
	}





	public AuthenticationToken(Object principal, Object credentials,
			Collection<? extends GrantedAuthority> authorities) {
		super(principal, credentials, authorities);
	}
}
