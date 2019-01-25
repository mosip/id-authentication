package io.mosip.kernel.auth.adapter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/***********************************************************************************************************************
 * AuthFilter is bound with AuthenticationManager to attempt authentication.
 *
 * Attempt Authentication tasks:
 * 1. Receives "Authorization" Header from request headers.
 * 2. Use the assigned Authentication manager to authenticate with the token.
 **********************************************************************************************************************/

public class AuthFilter extends AbstractAuthenticationProcessingFilter {

    protected AuthFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException {
        String token = httpServletRequest.getHeader("Authorization");
        if (token == null) {
            throw new RuntimeException("Invalid Token");
        }

        AuthToken authToken = new AuthToken(token);
        return getAuthenticationManager().authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }
}
