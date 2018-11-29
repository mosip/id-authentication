package io.mosip.kernel.synchandler.config;



import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

import io.mosip.kernel.synchandler.filter.SecurityFilter;


@Configuration
@Order(102)
public class SecurityConfig extends WebSecurityConfigurerAdapter {



	@Bean
	public HttpFirewall defaultHttpFirewall() {
		return new DefaultHttpFirewall();
	}


	@Override
	public void configure(WebSecurity webSecurity) throws Exception {
		webSecurity.ignoring().antMatchers(allowedEndPoints());
		super.configure(webSecurity);
		webSecurity.httpFirewall(defaultHttpFirewall());
	}


	private String[] allowedEndPoints() {
		return new String[] {
				"/assets/**",
				"/icons/**",
				"/screenshots/**",
				"/favicon**",
				"/**/favicon**",
				"/css/**",
				"/js/**",
				"/*/error**",
				"/*/webjars/**",
				"/*/v2/api-docs",
				"/*/configuration/ui",
				"/*/configuration/security",
				"/*/swagger-resources/**",
				"/*/swagger-ui.html"
		};
	}


	@Override
	protected void configure(final HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable().authorizeRequests().anyRequest().authenticated().and()
				.addFilterBefore(new SecurityFilter(), BasicAuthenticationFilter.class)
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().exceptionHandling()
				.authenticationEntryPoint(unauthorizedEntryPoint());
	}


	@Bean
	public AuthenticationEntryPoint unauthorizedEntryPoint() {
		return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}
}