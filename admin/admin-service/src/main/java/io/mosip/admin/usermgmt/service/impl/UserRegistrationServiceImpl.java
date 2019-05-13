package io.mosip.admin.usermgmt.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.admin.usermgmt.dto.RidVerificationRequestDto;
import io.mosip.admin.usermgmt.dto.UserRegistrationRequestDto;
import io.mosip.admin.usermgmt.dto.UserRegistrationResponseDto;
import io.mosip.admin.usermgmt.service.UserRegistrationService;
import io.mosip.admin.usermgmt.util.UserMgmtUtil;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.http.RequestWrapper;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private UserMgmtUtil validateRidUtil;
	
	@Autowired
	private ObjectMapper mapper;

	@Override
	public UserRegistrationResponseDto register(UserRegistrationRequestDto request) {
		try {
			String authUserRegistrationUrl = "http://localhost:8091/v1/authmanager/user";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			RequestWrapper<UserRegistrationRequestDto> userRegistrationRequestDto = new RequestWrapper<>();
			userRegistrationRequestDto.setRequest(request);
			HttpEntity<RequestWrapper<UserRegistrationRequestDto>> userHttpEntity = new HttpEntity<>(
					userRegistrationRequestDto, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(authUserRegistrationUrl, userHttpEntity,
					String.class);
			String responseBody=response.getBody();
			System.out.println(ExceptionUtils.getServiceErrorList(responseBody));
			JsonNode authResponse=mapper.readTree(responseBody);
			authResponse=authResponse.get("response");
			String responseStatus=authResponse.get("status").asText();
			
			if(responseStatus.equals("SUCCESS")) {
				headers.setContentType(MediaType.MULTIPART_FORM_DATA);
				MultiValueMap<Object, Object> emailMap = new LinkedMultiValueMap<>();
				emailMap.add("mailContent", "YOUR REGISTRATION ACTIVATION LINK IS " + request.getRidValidationUrl()
						+ "?username=" + request.getUserName());
				emailMap.add("mailSubject", "ADMIN USER REGISTRATION ALERT");
				emailMap.add("mailTo", request.getEmailID());
				HttpEntity<MultiValueMap<Object, Object>> httpEntity = new HttpEntity<>(emailMap, headers);

				String url = "https://qa.mosip.io/v1/emailnotifier/email/send";
				restTemplate.postForEntity(url, httpEntity, Object.class);
			}else {
				//exception
			}
		

		} catch (Exception e) {
			System.out.println(e);
		}
		UserRegistrationResponseDto dto = new UserRegistrationResponseDto("SUCCESS");
		return dto;
	}

	@Override
	public UserRegistrationResponseDto ridVerification(RidVerificationRequestDto request) {
		String authSendOtpUrl = "https://dev.mosip.io/v1/authmanager/authenticate/sendotp";
		String demoAuthResponse = validateRidUtil.validateUserRid(request.getRid(), request.getUserName());
		UserRegistrationResponseDto ridVerificationResponse = new UserRegistrationResponseDto();
		if (demoAuthResponse.equals("SUCCESS")) {
			// sendOTP
			// restTemplate.postForEntity(authSendOtpUrl, httpEntity, Object.class);

			ridVerificationResponse.setStatus("SUCCESS");

		} else {
			ridVerificationResponse.setStatus("FAILURE");
		}
		return ridVerificationResponse;
	}

}
