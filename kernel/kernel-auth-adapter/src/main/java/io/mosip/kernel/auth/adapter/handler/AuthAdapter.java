package io.mosip.kernel.auth.adapter.handler;

import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

public class AuthAdapter extends KeycloakWebSecurityConfigurerAdapter {

	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void configure(WebSecurity web) throws Exception {

		super.configure(web);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		super.configure(http);
	}
}
