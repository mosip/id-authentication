package io.mosip.kernel.auth.adapter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

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
 * 3. Endpoints that need to be exempted for authentication are put here.
 *
 * @author Sabbu Uday Kumar
 * @since 1.0.0
 **********************************************************************************************************************/

public class AuthFilter extends AbstractAuthenticationProcessingFilter {

    private RequestMatcher requestMatcher;

    private String[] allowedEndPoints() {
        return new String[]{
                "/assets/**",
                "/icons/**",
                "/screenshots/**",
                "/favicon**",
                "/**/favicon**",
                "/css/**",
                "/js/**",
                "/**/error**",
                "/**/webjars/**",
                "/**/v2/api-docs",
                "/**/configuration/ui",
                "/**/configuration/security",
                "/**/swagger-resources/**",
                "/**/swagger-ui.html"
        };
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