/**
 * 
 */
package io.mosip.kernel.auth.adapter;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author M1049825
 *
 */
public class AuthFilter extends AbstractAuthenticationProcessingFilter {

	private RequestMatcher requestMatcher;

	private String[] allowedEndPoints() {
		return new String[] { "/assets/**", "/icons/**", "/screenshots/**", "/favicon**", "/**/favicon**", "/css/**",
				"/js/**", "/**/error**", "/**/webjars/**", "/**/v2/api-docs", "/**/configuration/ui",
				"/**/configuration/security", "/**/swagger-resources/**", "/**/swagger-ui.html","/**/login/**" };
	}

	protected AuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
		super(requiresAuthenticationRequestMatcher);
		this.requestMatcher = requiresAuthenticationRequestMatcher;
	}

	@Override
	protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
		String[] endpoints = allowedEndPoints();
		for (String pattern : endpoints) {
			RequestMatcher ignorePattern = new AntPathRequestMatcher(pattern);
			if (ignorePattern.matches(request)) {
				return false;
			}
		}

		return true;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws AuthenticationException {
		String token = null;
		Cookie[] cookies = httpServletRequest.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().contains(AuthAdapterConstant.AUTH_REQUEST_COOOKIE_HEADER)) {
					token = cookie.getValue();
				}
			}
		}
		if (token == null) {
			throw new RuntimeException(AuthAdapterConstant.AUTH_INVALID_TOKEN);
		}

		AuthToken authToken = new AuthToken(token);
		return getAuthenticationManager().authenticate(authToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		super.successfulAuthentication(request, response, chain, authResult);
		chain.doFilter(request, response);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		super.unsuccessfulAuthentication(request, response, failed);

	}
}