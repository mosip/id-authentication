package io.mosip.kernel.auth.adapter;


import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/***********************************************************************************************************************
 * Handles successful authentication.
 * If any action needs to be done after successful authentication, this is where you have to do it.
 **********************************************************************************************************************/

public class AuthSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        System.out.println("Successfully Authenticated");
    }
}
