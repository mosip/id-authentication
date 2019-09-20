package io.mosip.registration.processor.transaction.api.transaction.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Class PrintServiceSecurityConfig.
 * 
 * @author M1045447 Mono
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TransactionServiceSecurityConfig extends WebSecurityConfigurerAdapter {

	/**
	 * Default http firewall.
	 *
	 * @return the http firewall
	 */
	@Bean
	public HttpFirewall defaultHttpFirewall() {
		return new DefaultHttpFirewall();
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.WebSecurity)
	 */
	@Override
	public void configure(WebSecurity webSecurity) throws Exception {
		webSecurity.ignoring().antMatchers(allowedEndPoints());
		super.configure(webSecurity);
		webSecurity.httpFirewall(defaultHttpFirewall());
	}

	/**
	 * Allowed end points.
	 *
	 * @return the string[]
	 */
	private String[] allowedEndPoints() {
		return new String[] { "/assets/**", "/icons/**", "/screenshots/**", "/favicon**", "/**/favicon**", "/css/**",
				"/js/**", "/*/error**", "/*/webjars/**", "/*/v2/api-docs", "/*/configuration/ui",
				"/*/configuration/security", "/*/swagger-resources/**", "/*/swagger-ui.html" };
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
	 */
	@Override
	protected void configure(final HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable();
		httpSecurity.httpBasic().and().authorizeRequests().anyRequest().authenticated().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().exceptionHandling()
				.authenticationEntryPoint(unauthorizedEntryPoint());
	}

	/**
	 * Unauthorized entry point.
	 *
	 * @return the authentication entry point
	 */
	@Bean
	public AuthenticationEntryPoint unauthorizedEntryPoint() {
		return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#userDetailsService()
	 */
	@Bean
	public UserDetailsService userDetailsService() {
		List<UserDetails> users = new ArrayList<>();
		users.add(new User("reg-processor", "mosip",
				Arrays.asList(new SimpleGrantedAuthority("ROLE_REGISTRATION_PROCESSOR"))));
		users.add(new User("reg-admin", "mosip",
				Arrays.asList(new SimpleGrantedAuthority("ROLE_REGISTRATION_ADMIN"))));
		return new InMemoryUserDetailsManager(users);
	}
}