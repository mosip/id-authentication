package io.mosip.kernel.synchandler.entity;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthUser extends User {
	private static final long serialVersionUID = 3102294056807388456L;
	private String displayName = null;

	public AuthUser(String username, String displayName, String password,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		this.displayName = displayName;
	}

	private AuthUser() {
		super(null, null, null);
	}

}
