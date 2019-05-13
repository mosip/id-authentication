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

import io.mosip.admin.usermgmt.dto.RidVerificationRequestDto;
import io.mosip.admin.usermgmt.dto.SendOtpRequestDto;
import io.mosip.admin.usermgmt.dto.UserRegistrationRequestDto;
import io.mosip.admin.usermgmt.dto.UserRegistrationResponseDto;
import io.mosip.admin.usermgmt.exception.AdminServiceResponseException;
import io.mosip.admin.usermgmt.service.UserRegistrationService;
import io.mosip.admin.usermgmt.util.UserMgmtUtil;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private UserMgmtUtil validateRidUtil;

	@Override
	public UserRegistrationResponseDto register(UserRegistrationRequestDto request) {
		String authUserRegistrationUrl = "http://localhost:8091/v1/authmanager/user";
		String emailServiceApi = "https://qa.mosip.io/v1/emailnotifier/email/send";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RequestWrapper<UserRegistrationRequestDto> userRegistrationRequestDto = new RequestWrapper<>();
		userRegistrationRequestDto.setRequest(request);
		HttpEntity<RequestWrapper<UserRegistrationRequestDto>> userHttpEntity = new HttpEntity<>(
				userRegistrationRequestDto, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(authUserRegistrationUrl, userHttpEntity,
				String.class);
		String responseBody = response.getBody();
		List<ServiceError> validationErrorList = ExceptionUtils.getServiceErrorList(responseBody);
		if (!validationErrorList.isEmpty()) {
			throw new AdminServiceResponseException(validationErrorList);
		}
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultiValueMap<Object, Object> emailMap = new LinkedMultiValueMap<>();
		emailMap.add("mailContent",
				"YOUR RID SUBMISSION LINK IS " + request.getRidValidationUrl() + "?username=" + request.getUserName());
		emailMap.add("mailSubject", "ADMIN USER REGISTRATION ALERT");
		emailMap.add("mailTo", request.getEmailID());
		HttpEntity<MultiValueMap<Object, Object>> httpEntity = new HttpEntity<>(emailMap, headers);
		restTemplate.postForEntity(emailServiceApi, httpEntity, Object.class);

		return new UserRegistrationResponseDto("SUCCESS");
	}

	@Override
	public UserRegistrationResponseDto ridVerification(RidVerificationRequestDto request) {
		String authSendOtpUrl = "https://dev.mosip.io/v1/authmanager/authenticate/sendotp";
		SendOtpRequestDto requestDto = new SendOtpRequestDto();
		String demoAuthResponse = validateRidUtil.validateUserRid(request.getRid(), request.getUserName());
		UserRegistrationResponseDto ridVerificationResponse = new UserRegistrationResponseDto();
		if (demoAuthResponse.equals("SUCCESS")) {
			requestDto.setAppId("admin");
			requestDto.setContext("auth-otp");
			requestDto.setUserId(request.getUserName());
			requestDto.setUseridtype("USERID");
			List<String> channel = new ArrayList<>();
			channel.add("email");
			requestDto.setOtpChannel(channel);
			// sendOTP
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			RequestWrapper<SendOtpRequestDto> requestWrapper = new RequestWrapper<>();
			requestWrapper.setRequest(requestDto);
			HttpEntity<RequestWrapper<SendOtpRequestDto>> otpHttpEntity = new HttpEntity<>(requestWrapper, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(authSendOtpUrl, otpHttpEntity, String.class);
			List<ServiceError> validationErrorList = ExceptionUtils.getServiceErrorList(response.getBody());
			if (!validationErrorList.isEmpty()) {
				throw new AdminServiceResponseException(validationErrorList);
			}
			ridVerificationResponse.setStatus("SUCCESS");

		} else {
			ridVerificationResponse.setStatus("FAILURE");
		}
		return ridVerificationResponse;
	}

}
