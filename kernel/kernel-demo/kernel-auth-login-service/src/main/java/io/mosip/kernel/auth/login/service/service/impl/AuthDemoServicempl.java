package io.mosip.kernel.auth.login.service.service.impl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auth.login.service.dto.AuthNResponse;
import io.mosip.kernel.auth.login.service.dto.LoginUserDTO;
import io.mosip.kernel.auth.login.service.service.AuthDemoService;

@Service
public class AuthDemoServicempl implements AuthDemoService {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${kernel.auth.service.url}")
	private String authUrl;
	

	@Value("${kernel.auth.service.logout.url}")
	private String logoutUrl;




	@Override
	public ResponseEntity<AuthNResponse> authenticateUser(LoginUserDTO request) {

		HttpHeaders smsHeaders = new HttpHeaders();
		smsHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<LoginUserDTO> smshttpEntity = new HttpEntity<>(
				request, smsHeaders);
		return  restTemplate.exchange(authUrl, HttpMethod.POST,
				smshttpEntity, AuthNResponse.class);

	}
	
	
	@Override
	public ResponseEntity<AuthNResponse> logoutUser(HttpServletRequest request, HttpServletResponse res) {
		AuthNResponse authnResponse = new AuthNResponse();
		Cookie[] cookie = request.getCookies();
		String token = null;
		for (Cookie co : cookie) {
			if (co.getName().contains("Authorization")) {
				token = co.getValue();
			}
		}
		HttpHeaders smsHeaders = new HttpHeaders();
		smsHeaders.set("Cookie", "Authorization=" + token);
		HttpEntity<AuthNResponse> smshttpEntity = new HttpEntity<>(authnResponse, smsHeaders);
		ResponseEntity<AuthNResponse> resp=null;
				try {
					resp=restTemplate.exchange(logoutUrl, HttpMethod.POST, smshttpEntity, AuthNResponse.class);
				}catch(HttpClientErrorException | HttpServerErrorException e) {
					System.out.println(e);
				}
		
		return resp;

	}


}
