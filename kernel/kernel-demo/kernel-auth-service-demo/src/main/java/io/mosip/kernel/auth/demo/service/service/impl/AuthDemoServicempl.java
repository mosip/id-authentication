package io.mosip.kernel.auth.demo.service.service.impl;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auth.demo.service.constant.AuthDemoServiceErrorCode;
import io.mosip.kernel.auth.demo.service.dto.AuthDemoServiceRequestDto;
import io.mosip.kernel.auth.demo.service.dto.AuthDemoServiceResponseDto;
import io.mosip.kernel.auth.demo.service.dto.OtpRequestDto;
import io.mosip.kernel.auth.demo.service.dto.OtpResponseDto;
import io.mosip.kernel.auth.demo.service.exceeption.ParseResponseException;
import io.mosip.kernel.auth.demo.service.service.AuthDemoService;

@Service
public class AuthDemoServicempl implements AuthDemoService {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${mosip.kernel.smsnotifier-url}")
	private String smsUrl;


	@Override
	public AuthDemoServiceResponseDto addApplication(@Valid AuthDemoServiceRequestDto authDemoRequestDto) {
		return new AuthDemoServiceResponseDto("create application Request accepted successfully for application "+authDemoRequestDto.getApplicationId());
	}

	@Override
	public AuthDemoServiceResponseDto getApplication(String applicationId) {
		return new AuthDemoServiceResponseDto("get application Request accepted successfully for application "+applicationId);
	}

	@Override
	public AuthDemoServiceResponseDto deleteMapping(String applicationId) {
		return new AuthDemoServiceResponseDto("delete application Request accepted successfully for application "+applicationId);
	}

	@Override
	public AuthDemoServiceResponseDto updateApplication(@Valid AuthDemoServiceRequestDto authDemoRequestDto) {
		return new AuthDemoServiceResponseDto("update application Request accepted successfully for application "+authDemoRequestDto.getApplicationId());
	}

	@Override
	public OtpResponseDto sendOtp(@Valid OtpRequestDto otpRequestDto) {
	
		HttpHeaders smsHeaders = new HttpHeaders();
		smsHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<OtpRequestDto> smshttpEntity = new HttpEntity<>(
				otpRequestDto, smsHeaders);
		ResponseEntity<String> response = restTemplate.exchange(smsUrl, HttpMethod.POST,
				smshttpEntity, String.class);

		String responseBody = response.getBody();
		System.out.println(smsUrl);
		System.out.println(responseBody);
		
		OtpResponseDto smsResponseDto;
		try {
			smsResponseDto = objectMapper.readValue(response.getBody(),
					OtpResponseDto.class);
		} catch (IOException e) {
			throw new ParseResponseException(AuthDemoServiceErrorCode.RESPONSE_PARSE_ERROR.getErrorCode(),
					AuthDemoServiceErrorCode.RESPONSE_PARSE_ERROR.getErrorMessage() + e.getMessage(), e);
		}
		
		return smsResponseDto;
	}


}
