package io.mosip.kernel.auth.login.service.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.mosip.kernel.auth.login.service.dto.AuthNResponse;
import io.mosip.kernel.auth.login.service.dto.LoginUserDTO;


@Service
public interface AuthDemoService {

	ResponseEntity<AuthNResponse> authenticateUser(LoginUserDTO request);
	
	ResponseEntity<AuthNResponse>  logoutUser(HttpServletRequest request, HttpServletResponse res);




}