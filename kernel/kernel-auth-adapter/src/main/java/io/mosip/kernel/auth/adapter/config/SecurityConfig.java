package io.mosip.kernel.auth.adapter.config;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import io.mosip.kernel.auth.adapter.filter.AuthFilter;
import io.mosip.kernel.auth.adapter.filter.CorsFilter;
import io.mosip.kernel.auth.adapter.handler.AuthHandler;
import io.mosip.kernel.auth.adapter.handler.AuthSuccessHandler;

/**
 * Holds the main configuration for authentication and authorization using
 * spring security.
 *
 * Inclusions: 1. AuthenticationManager bean configuration: a. This is assigned
 * an authProvider that we implemented. This option can include multiple auth
 * providers if necessary based on the requirement. b. RETURNS an instance of
 * the ProviderManager. 2. AuthFilter bean configuration: a. This extends
 * AbstractAuthenticationProcessingFilter. b. Instance of the AuthFilter is
 * created. c. This filter comes in line after the AuthHeadersFilter. d. Binds
 * the AuthenticationManager instance created with the filter. e. Binds the
 * AuthSuccessHandler created with the filter. f. RETURNS an instance of the
 * AuthFilter. 3. RestTemplate bean configuration: a. Binds the
 * ClientInterceptor instance with the RestTemplate instance created. b. RETURNS
 * an instance of the RestTemplate. 4. Secures endpoints using antMatchers and
 * adds filters in a sequence for execution.
 *
 * @author Sabbu Uday Kumar
 * @author Ramadurai Saravana Pandian
 * @author Raj Jha 
 * 
 * @since 1.0.0
 **/

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(2)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthHandler authProvider;

	@Bean
	public AuthenticationManager authenticationManager() {
		return new ProviderManager(Collections.singletonList(authProvider));
	}

	@Bean
	public AuthFilter authFilter() {
		RequestMatcher requestMatcher = new AntPathRequestMatcher("*");
		AuthFilter filter = new AuthFilter(requestMatcher);
		filter.setAuthenticationManager(authenticationManager());
		filter.setAuthenticationSuccessHandler(new AuthSuccessHandler());
		return filter;
	}
	

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers("*").authenticated().and().exceptionHandling()
				.authenticationEntryPoint(new AuthEntryPoint()).and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.addFilterBefore(authFilter(), UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(new CorsFilter(), AuthFilter.class);
		http.headers().cacheControl();
	}
	
	@Bean
    public FilterRegistrationBean<AuthFilter> registration(AuthFilter filter) {
        FilterRegistrationBean<AuthFilter> registration = new FilterRegistrationBean<AuthFilter>(filter);
        registration.setEnabled(false);
        return registration;
    }

}

class AuthEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			AuthenticationException e) throws IOException {
		httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED");
	}
	

}