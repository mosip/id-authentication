package io.mosip.kernel.auth.adapter.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class AuthSuccessHandler implements AuthenticationSuccessHandler {

             
             @Override
             public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                          Authentication authentication) throws IOException, ServletException {
             //System.out.println("success handler");

             }

       }
