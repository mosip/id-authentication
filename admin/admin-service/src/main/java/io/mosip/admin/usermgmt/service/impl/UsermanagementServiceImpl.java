package io.mosip.admin.usermgmt.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.admin.usermgmt.dto.RidVerificationRequestDto;
import io.mosip.admin.usermgmt.dto.RidVerificationResponseDto;
import io.mosip.admin.usermgmt.dto.SendOtpRequestDto;
import io.mosip.admin.usermgmt.dto.UserPasswordRequestDto;
import io.mosip.admin.usermgmt.dto.UserPasswordResponseDto;
import io.mosip.admin.usermgmt.dto.UserRegistrationRequestDto;
import io.mosip.admin.usermgmt.dto.UserRegistrationResponseDto;
import io.mosip.admin.usermgmt.service.UsermanagementService;
import io.mosip.admin.usermgmt.util.UserMgmtUtil;
import io.mosip.kernel.core.http.RequestWrapper;

/**
 * @author Urvil Joshi
 * @author Ritesh Sinha
 *
 */
@Service
public class UsermanagementServiceImpl implements UsermanagementService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("${auth.server.user-register-url}")
	private String authUserRegistrationURL;
	@Value("${mosip.kernel.emailnotifier-url}")
	private String emailServiceApiURL;
	@Value("${auth.server.sendotp-url}")
	private String authSendOtpURL;
	@Value("${auth.server.user-add-password-url}")
	private String passwordURL;
	
	@Value("${mosip.admin-appid}")
	private String addID;
	
	@Value("${mosip.admin-otp-context}")
	private String otpContext;
	
	@Value("${mosip.admin-userid-otp-type}")
	private String userIDType;

	@Override
	public UserRegistrationResponseDto register(UserRegistrationRequestDto registrationRequestDto) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RequestWrapper<UserRegistrationRequestDto> userRegistrationRequestDto = new RequestWrapper<>();
		userRegistrationRequestDto.setRequest(registrationRequestDto);
		HttpEntity<RequestWrapper<UserRegistrationRequestDto>> userHttpEntity = new HttpEntity<>(
				userRegistrationRequestDto, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(authUserRegistrationURL, userHttpEntity,
				String.class);
		UserMgmtUtil.throwExceptionIfExist(response);

		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultiValueMap<Object, Object> emailMap = new LinkedMultiValueMap<>();
		emailMap.add("mailContent", "YOUR RID SUBMISSION LINK IS " + registrationRequestDto.getRidValidationUrl()
				+ "?username=" + registrationRequestDto.getUserName());
		emailMap.add("mailSubject", "ADMIN USER REGISTRATION ALERT");
		emailMap.add("mailTo", registrationRequestDto.getEmailID());
		HttpEntity<MultiValueMap<Object, Object>> httpEntity = new HttpEntity<>(emailMap, headers);
		ResponseEntity<String> responseEmailService = restTemplate.postForEntity(emailServiceApiURL, httpEntity,
				String.class);

		UserMgmtUtil.throwExceptionIfExist(responseEmailService);

		return UserMgmtUtil.getResponse(objectMapper, response, UserRegistrationResponseDto.class);
	}

	@Override
	public RidVerificationResponseDto ridVerification(RidVerificationRequestDto ridVerificationRequestDto) {

		SendOtpRequestDto requestDto = new SendOtpRequestDto();
		// validate rid
		UserMgmtUtil.validateUserRid(ridVerificationRequestDto.getRid(), ridVerificationRequestDto.getUserName());
		requestDto.setAppId(addID);
		requestDto.setContext(otpContext);
		requestDto.setUserId(ridVerificationRequestDto.getUserName());
		requestDto.setUseridtype(userIDType);
		List<String> channel = new ArrayList<>();
		channel.add("email");
		requestDto.setOtpChannel(channel);
		// sendOTP
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RequestWrapper<SendOtpRequestDto> requestWrapper = new RequestWrapper<>();
		requestWrapper.setRequest(requestDto);
		HttpEntity<RequestWrapper<SendOtpRequestDto>> otpHttpEntity = new HttpEntity<>(requestWrapper, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(authSendOtpURL, otpHttpEntity, String.class);
		UserMgmtUtil.throwExceptionIfExist(response);
		RidVerificationResponseDto responseDto=new RidVerificationResponseDto();
		responseDto.setUserName(ridVerificationRequestDto.getUserName());
		return responseDto;
		//return UserMgmtUtil.getResponse(objectMapper, response, RidVerificationResponseDto.class);
	}

	@Override
	public UserPasswordResponseDto addPassword(UserPasswordRequestDto userPasswordRequestDto) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RequestWrapper<UserPasswordRequestDto> requestWrapper = new RequestWrapper<>();
		requestWrapper.setRequest(userPasswordRequestDto);
		HttpEntity<RequestWrapper<UserPasswordRequestDto>> otpHttpEntity = new HttpEntity<>(requestWrapper, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(passwordURL, otpHttpEntity, String.class);
		UserMgmtUtil.throwExceptionIfExist(response);
		return UserMgmtUtil.getResponse(objectMapper, response, UserPasswordResponseDto.class);
	}

}
