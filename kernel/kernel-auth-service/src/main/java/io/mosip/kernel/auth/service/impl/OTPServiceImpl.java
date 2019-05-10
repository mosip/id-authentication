package io.mosip.kernel.auth.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.entities.AuthNResponseDto;
import io.mosip.kernel.auth.entities.BasicTokenDto;
import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.MosipUserDtoToken;
import io.mosip.kernel.auth.entities.otp.OtpEmailSendResponseDto;
import io.mosip.kernel.auth.entities.otp.OtpGenerateRequest;
import io.mosip.kernel.auth.entities.otp.OtpGenerateRequestDto;
import io.mosip.kernel.auth.entities.otp.OtpGenerateResponseDto;
import io.mosip.kernel.auth.entities.otp.OtpSmsSendRequestDto;
import io.mosip.kernel.auth.entities.otp.OtpTemplateDto;
import io.mosip.kernel.auth.entities.otp.OtpTemplateResponseDto;
import io.mosip.kernel.auth.entities.otp.OtpUser;
import io.mosip.kernel.auth.entities.otp.OtpValidatorResponseDto;
import io.mosip.kernel.auth.entities.otp.SmsResponseDto;
import io.mosip.kernel.auth.entities.otp.email.OTPEmailTemplate;
import io.mosip.kernel.auth.exception.AuthManagerException;
import io.mosip.kernel.auth.exception.AuthManagerServiceException;
import io.mosip.kernel.auth.jwtBuilder.TokenGenerator;
import io.mosip.kernel.auth.service.OTPGenerateService;
import io.mosip.kernel.auth.service.OTPService;
import io.mosip.kernel.auth.service.TemplateUtil;
import io.mosip.kernel.auth.service.TokenGenerationService;
import io.mosip.kernel.auth.validator.AuthOtpValidator;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;

/**
 * @author Ramadurai Pandian
 *
 */
@Component
public class OTPServiceImpl implements OTPService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.auth.service.OTPService#sendOTP(io.mosip.kernel.auth.
	 * entities.MosipUserDto, java.lang.String)
	 */

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	MosipEnvironment mosipEnvironment;

	@Autowired
	TokenGenerator tokenGenerator;

	@Autowired
	OTPGenerateService oTPGenerateService;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private TokenGenerationService tokenService;
	
	@Autowired
	private TemplateUtil templateUtil;
	
	@Autowired
	private AuthOtpValidator authOtpValidator;

	@Override
	public AuthNResponseDto sendOTP(MosipUserDto mosipUserDto, List<String> otpChannel, String appId) {
		AuthNResponseDto authNResponseDto = null;
		OtpEmailSendResponseDto otpEmailSendResponseDto = null;
		SmsResponseDto otpSmsSendResponseDto = null;
		String emailMessage = null,mobileMessage = null;
		String token=null;
		try {
			token = tokenService.getInternalTokenGenerationService();
		} catch (Exception e) {
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),e.getMessage());
		}
		OtpGenerateResponseDto otpGenerateResponseDto = oTPGenerateService.generateOTP(mosipUserDto,token);
		if(otpGenerateResponseDto!=null && otpGenerateResponseDto.getStatus().equals("USER_BLOCKED"))
		{
			authNResponseDto = new AuthNResponseDto();
			authNResponseDto.setStatus(AuthConstant.FAILURE_STATUS);
			authNResponseDto.setMessage(otpGenerateResponseDto.getStatus());
			return authNResponseDto;
		}
		for(String channel:otpChannel)
		{
			switch(channel)
			{
			case AuthConstant.EMAIL:
				emailMessage = getOtpEmailMessage(otpGenerateResponseDto, appId,token);
				otpEmailSendResponseDto = sendOtpByEmail(emailMessage, mosipUserDto.getMail(),token);
				break;
			case AuthConstant.PHONE:
				mobileMessage = getOtpSmsMessage(otpGenerateResponseDto, appId,token);
				otpSmsSendResponseDto = sendOtpBySms(mobileMessage, mosipUserDto.getMobile(),token);
				break;
			}		
		}
		if (otpEmailSendResponseDto != null) {
			authNResponseDto = new AuthNResponseDto();
			authNResponseDto.setStatus(otpEmailSendResponseDto.getStatus());
			authNResponseDto.setMessage(otpEmailSendResponseDto.getMessage());
		}
		if (otpSmsSendResponseDto != null) {
			authNResponseDto = new AuthNResponseDto();
			authNResponseDto.setStatus(otpSmsSendResponseDto.getStatus());
			authNResponseDto.setMessage(otpSmsSendResponseDto.getMessage());
		}
		return authNResponseDto;
	}

	private String getOtpEmailMessage(OtpGenerateResponseDto otpGenerateResponseDto, String appId, String token) {
			String template = null;
			OtpTemplateResponseDto otpTemplateResponseDto = null;
			
			final String url = mosipEnvironment.getMasterDataTemplateApi()
					+"/"+ mosipEnvironment.getPrimaryLanguage() + mosipEnvironment.getMasterDataOtpTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.set(AuthConstant.COOKIE, AuthConstant.AUTH_HEADER+token);
			ResponseEntity<String> response = restTemplate.exchange(url,HttpMethod.GET,new HttpEntity<Object>(headers), String.class);
			if (response.getStatusCode().equals(HttpStatus.OK)) {
				String responseBody = response.getBody();
				List<ServiceError> validationErrorsList = null;
					validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);  
				if (!validationErrorsList.isEmpty()) {
					throw new AuthManagerServiceException(validationErrorsList);
				}
				ResponseWrapper<?> responseObject;
				try {
					responseObject = mapper.readValue(response.getBody(), ResponseWrapper.class);
					otpTemplateResponseDto = mapper.readValue(mapper.writeValueAsString(responseObject.getResponse()), OtpTemplateResponseDto.class);
				}catch(Exception e)
				{
					throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),e.getMessage());
				}
			}
			List<OtpTemplateDto> otpTemplateList = otpTemplateResponseDto.getTemplates();
			for (OtpTemplateDto otpTemplateDto : otpTemplateList) {
				if (otpTemplateDto.getId().toLowerCase().equals(appId.toLowerCase())) {
					template = otpTemplateDto.getFileText();

				}
			}
			String otp = otpGenerateResponseDto.getOtp();
			template = template.replace("$otp", otp);
			return template;
	}

	private String getOtpSmsMessage(OtpGenerateResponseDto otpGenerateResponseDto, String appId, String token) {
		try {
			final String url = mosipEnvironment.getMasterDataTemplateApi()
					+"/"+ mosipEnvironment.getPrimaryLanguage() + mosipEnvironment.getMasterDataOtpTemplate();
			OtpTemplateResponseDto otpTemplateResponseDto = null;	
			HttpHeaders headers = new HttpHeaders();
			headers.set(AuthConstant.COOKIE, AuthConstant.AUTH_HEADER+token);
			ResponseEntity<String> response = restTemplate.exchange(url,HttpMethod.GET,new HttpEntity<Object>(headers), String.class);
			if (response.getStatusCode().equals(HttpStatus.OK)) {
				String responseBody = response.getBody();
				List<ServiceError> validationErrorsList = null;
					validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);  
				if (!validationErrorsList.isEmpty()) {
					throw new AuthManagerServiceException(validationErrorsList);
				}
				ResponseWrapper<?> responseObject;
				try {
					responseObject = mapper.readValue(response.getBody(), ResponseWrapper.class);
					otpTemplateResponseDto = mapper.readValue(mapper.writeValueAsString(responseObject.getResponse()), OtpTemplateResponseDto.class);
				}catch(Exception e)
				{
					throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),e.getMessage());
				}
			}
			String template = null;
			List<OtpTemplateDto> otpTemplateList = otpTemplateResponseDto.getTemplates();
			for (OtpTemplateDto otpTemplateDto : otpTemplateList) {
				if (otpTemplateDto.getId().toLowerCase().equals(appId.toLowerCase())) {
					template = otpTemplateDto.getFileText();

				}
			}
			String otp = otpGenerateResponseDto.getOtp();
			template = template.replace("$otp", otp);
			return template;
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			String message = e.getResponseBodyAsString();
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),message);
		}
	}

	private OtpEmailSendResponseDto sendOtpByEmail(String message, String email, String token) {
			ResponseEntity<String> response = null;	
			String url = mosipEnvironment.getOtpSenderEmailApi();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
			OtpEmailSendResponseDto otpEmailSendResponseDto = null;
			headers.set(AuthConstant.COOKIE, AuthConstant.AUTH_HEADER+token);
			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("mailTo", email);
			map.add("mailSubject", "MOSIP Notification");
			map.add("mailContent",message);
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
			try
			{
				try {
					response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
				} catch(HttpServerErrorException | HttpClientErrorException e)
				{
					String error = e.getResponseBodyAsString();
				} catch (RestClientException e) {
					e.printStackTrace();
				}
			}catch(HttpServerErrorException | HttpClientErrorException e)
			{
				String error = e.getResponseBodyAsString();
			}
			
			if (response.getStatusCode().equals(HttpStatus.OK)) {
				String responseBody = response.getBody();
				List<ServiceError> validationErrorsList = null;
					validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);  
				if (!validationErrorsList.isEmpty()) {
					throw new AuthManagerServiceException(validationErrorsList);
				}
				ResponseWrapper<?> responseObject;
				try {
					responseObject = mapper.readValue(response.getBody(), ResponseWrapper.class);
					otpEmailSendResponseDto = mapper.readValue(mapper.writeValueAsString(responseObject.getResponse()), OtpEmailSendResponseDto.class);
				}catch(Exception e)
				{
					throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),e.getMessage());
				}
			}
			return otpEmailSendResponseDto;
	}

	private SmsResponseDto sendOtpBySms(String message, String mobile, String token) {
		try {
			List<ServiceError> validationErrorsList = null;
			OtpSmsSendRequestDto otpSmsSendRequestDto = new OtpSmsSendRequestDto(mobile, message);
			SmsResponseDto otpSmsSendResponseDto=null;
			String url = mosipEnvironment.getOtpSenderSmsApi();
			RequestWrapper<OtpSmsSendRequestDto> reqWrapper = new RequestWrapper<>();
			reqWrapper.setRequesttime(LocalDateTime.now());
			reqWrapper.setRequest(otpSmsSendRequestDto);
			HttpHeaders headers = new HttpHeaders();
			headers.set(AuthConstant.COOKIE, AuthConstant.AUTH_HEADER+token);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST,new HttpEntity<Object>(reqWrapper,headers),
					String.class);	
			validationErrorsList = ExceptionUtils.getServiceErrorList(response.getBody());  
			if (!validationErrorsList.isEmpty()) {
				throw new AuthManagerServiceException(validationErrorsList);
			}
			ResponseWrapper<?> responseObject;
			try {
				responseObject = mapper.readValue(response.getBody(), ResponseWrapper.class);
				otpSmsSendResponseDto= mapper.readValue(mapper.writeValueAsString(responseObject.getResponse()), SmsResponseDto.class);
			}catch(Exception e)
			{
				throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),e.getMessage());
			}
			return otpSmsSendResponseDto;
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			String errmessage = e.getResponseBodyAsString();
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),errmessage);
		}
	}

	@Override
	public MosipUserDtoToken validateOTP(MosipUserDto mosipUser, String otp) {
		String key = new OtpGenerateRequest(mosipUser).getKey();
		MosipUserDtoToken mosipUserDtoToken = null;
		ResponseEntity<String> response = null;
		final String url = mosipEnvironment.getVerifyOtpUserApi();
		String token=null;
		try {
			token = tokenService.getInternalTokenGenerationService();
		} catch (Exception e) {
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),e.getMessage());
		}
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParam("key", key).queryParam("otp",
				otp);
		HttpHeaders headers = new HttpHeaders();
		headers.set(AuthConstant.COOKIE, AuthConstant.AUTH_HEADER+token);
		response = restTemplate.exchange(builder.toUriString(),HttpMethod.GET,new HttpEntity<Object>(headers), String.class);
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			String responseBody = response.getBody();
			List<ServiceError> validationErrorsList = null;
				validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);
	        
			if (!validationErrorsList.isEmpty()) {
				throw new AuthManagerServiceException(validationErrorsList);
			}
			OtpValidatorResponseDto otpResponse = null;
			ResponseWrapper<?> responseObject;
			try {
				responseObject = mapper.readValue(response.getBody(), ResponseWrapper.class);
				otpResponse = mapper.readValue(mapper.writeValueAsString(responseObject.getResponse()), OtpValidatorResponseDto.class);
			}catch(Exception e)
			{
				throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),e.getMessage());
			}
			if(otpResponse.getStatus()!=null && otpResponse.getStatus().equals("success"))
			{
				BasicTokenDto basicToken = tokenGenerator.basicGenerateOTPToken(mosipUser, true);
				mosipUserDtoToken = new MosipUserDtoToken(mosipUser, basicToken.getAuthToken(),
						basicToken.getRefreshToken(), basicToken.getExpiryTime(), null,null);
				mosipUserDtoToken.setMessage(otpResponse.getMessage());
				mosipUserDtoToken.setStatus(otpResponse.getStatus());
			}
			else
			{
				mosipUserDtoToken = new MosipUserDtoToken();
				mosipUserDtoToken.setMessage(otpResponse.getMessage());
				mosipUserDtoToken.setStatus(otpResponse.getStatus());
			}
			
		}
		return mosipUserDtoToken;
	}

	@Override
	public AuthNResponseDto sendOTPForUin(MosipUserDto mosipUserDto, List<String> otpChannel, String appId) {
		AuthNResponseDto authNResponseDto = null;
		OtpEmailSendResponseDto otpEmailSendResponseDto = null;
		SmsResponseDto otpSmsSendResponseDto = null;
		String emailMessage = null,mobileMessage = null;
		String token=null;
		try {
			token = tokenService.getInternalTokenGenerationService();
		} catch (Exception e) {
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),e.getMessage());
		}
		OtpGenerateResponseDto otpGenerateResponseDto = oTPGenerateService.generateOTP(mosipUserDto,token);
		for(String channel:otpChannel)
		{
			switch(channel)
			{
			case AuthConstant.EMAIL:
				emailMessage = getOtpEmailMessage(otpGenerateResponseDto, appId,token);
				otpEmailSendResponseDto = sendOtpByEmail(emailMessage, mosipUserDto.getMail(),token);
			case AuthConstant.PHONE:
				mobileMessage = getOtpSmsMessage(otpGenerateResponseDto, appId,token);
				otpSmsSendResponseDto = sendOtpBySms(mobileMessage, mosipUserDto.getMobile(),token);
			}		
		}
		if(otpEmailSendResponseDto!=null && otpSmsSendResponseDto!=null)
		{
			AuthNResponseDto authResponseDto = new AuthNResponseDto();
			authResponseDto.setMessage(AuthConstant.UIN_NOTIFICATION_MESSAGE);
		}
		return authNResponseDto;
	}
	
	private String getOtpMessage(String appId) {
		String template = null;
		OtpTemplateResponseDto otpTemplateResponseDto = null;
		final String url = mosipEnvironment.getMasterDataTemplateApi()
				+"/"+ mosipEnvironment.getPrimaryLanguage() + mosipEnvironment.getMasterDataOtpTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			String responseBody = response.getBody();
			List<ServiceError> validationErrorsList = null;
				validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);  
			if (!validationErrorsList.isEmpty()) {
				throw new AuthManagerServiceException(validationErrorsList);
			}
			try {
				otpTemplateResponseDto = mapper.readValue(responseBody, OtpTemplateResponseDto.class);
			}catch(Exception e)
			{
				throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),e.getMessage());
			}
		}
		List<OtpTemplateDto> otpTemplateList = otpTemplateResponseDto.getTemplates();
		for (OtpTemplateDto otpTemplateDto : otpTemplateList) {
			if (otpTemplateDto.getId().toLowerCase().equals(appId.toLowerCase())) {
				template = otpTemplateDto.getFileText();

			}
		}
		return template;
}

	@Override
	public AuthNResponseDto sendOTP(MosipUserDto mosipUser, OtpUser otpUser) {
		AuthNResponseDto authNResponseDto = null;
		OtpEmailSendResponseDto otpEmailSendResponseDto = null;
		SmsResponseDto otpSmsSendResponseDto = null;
		String mobileMessage = null;
		OTPEmailTemplate emailTemplate=null;
		String token=null;
		authOtpValidator.validateOTPUser(otpUser);
		try {
			token = tokenService.getInternalTokenGenerationService();
		} catch (Exception e) {
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),e.getMessage());
		}
		OtpGenerateResponseDto otpGenerateResponseDto = oTPGenerateService.generateOTPMultipleChannels(mosipUser,otpUser,token);
		for(String channel:otpUser.getOtpChannel())
		{
			switch(channel)
			{
			case AuthConstant.EMAIL:
				emailTemplate = templateUtil.getEmailTemplate(otpGenerateResponseDto.getOtp(),otpUser,token);
				otpEmailSendResponseDto = sendOtpByEmail(emailTemplate, mosipUser.getMail(),token);
				break;
			case AuthConstant.PHONE:
				mobileMessage =templateUtil.getOtpSmsMessage(otpGenerateResponseDto.getOtp(), otpUser,token);
				otpSmsSendResponseDto = sendOtpBySms(mobileMessage, mosipUser.getMobile(),token);
				break;
			}		
		}
		
		if(otpEmailSendResponseDto!=null && otpSmsSendResponseDto!=null)
		{
			authNResponseDto = new AuthNResponseDto();
			authNResponseDto.setStatus(AuthConstant.SUCCESS_STATUS);
			authNResponseDto.setMessage(AuthConstant.ALL_CHANNELS_MESSAGE);
		}
		else if (otpEmailSendResponseDto != null) {
			authNResponseDto = new AuthNResponseDto();
			authNResponseDto.setStatus(otpEmailSendResponseDto.getStatus());
			authNResponseDto.setMessage(otpEmailSendResponseDto.getMessage());
		}
		else if (otpSmsSendResponseDto != null) {
			authNResponseDto = new AuthNResponseDto();
			authNResponseDto.setStatus(otpSmsSendResponseDto.getStatus());
			authNResponseDto.setMessage(otpSmsSendResponseDto.getMessage());
		}
		return authNResponseDto;
	}
	
	private OtpEmailSendResponseDto sendOtpByEmail(OTPEmailTemplate emailTemplate, String email, String token) {
		ResponseEntity<String> response = null;	
		String url = mosipEnvironment.getOtpSenderEmailApi();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
		OtpEmailSendResponseDto otpEmailSendResponseDto = null;
		headers.set(AuthConstant.COOKIE, AuthConstant.AUTH_HEADER+token);
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("mailTo", email);
		map.add("mailSubject", emailTemplate.getEmailSubject());
		map.add("mailContent",emailTemplate.getEmailContent());
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		try {
				response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
			} catch(HttpServerErrorException | HttpClientErrorException e)
			{
				String error = e.getResponseBodyAsString();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			String responseBody = response.getBody();
			List<ServiceError> validationErrorsList = null;
				validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);  
			if (!validationErrorsList.isEmpty()) {
				throw new AuthManagerServiceException(validationErrorsList);
			}
			ResponseWrapper<?> responseObject;
			try {
				responseObject = mapper.readValue(response.getBody(), ResponseWrapper.class);
				otpEmailSendResponseDto = mapper.readValue(mapper.writeValueAsString(responseObject.getResponse()), OtpEmailSendResponseDto.class);
			}catch(Exception e)
			{
				throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),e.getMessage());
			}
		}
		return otpEmailSendResponseDto;
}
}