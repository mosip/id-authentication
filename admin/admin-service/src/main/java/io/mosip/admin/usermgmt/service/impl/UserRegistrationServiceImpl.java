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

import io.mosip.admin.usermgmt.dto.UserRegistrationRequestDto;
import io.mosip.admin.usermgmt.dto.UserRegistrationResponseDto;
import io.mosip.admin.usermgmt.service.UserRegistrationService;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public UserRegistrationResponseDto register(UserRegistrationRequestDto request) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			MultiValueMap<Object, Object> emailMap = new LinkedMultiValueMap<>();
			emailMap.add("mailContent", "YOUR REGISTRATION ACTIVATION LINK IS " + request.getRidValidationUrl() + "?username="
					+ request.getUserName());
			emailMap.add("mailSubject", "ADMIN USER REGISTRATION ALERT");
			emailMap.add("mailTo", request.getEmailID());
			HttpEntity<MultiValueMap<Object, Object>> httpEntity = new HttpEntity<>(emailMap, headers);

			String url = "https://qa.mosip.io/v1/emailnotifier/email/send";
			 restTemplate.postForEntity(url, httpEntity, Object.class);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		UserRegistrationResponseDto dto = new UserRegistrationResponseDto("SUCCESS");
		return dto;
	}

}
