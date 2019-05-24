/**
 * 
 */
package io.mosip.kernel.auth.service.impl;

import io.mosip.kernel.auth.adapter.exception.AuthNException;
import io.mosip.kernel.auth.adapter.exception.AuthZException;
import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.constant.AuthErrorCode;
import io.mosip.kernel.auth.constant.OTPErrorCode;
import io.mosip.kernel.auth.dto.MosipUserDto;
import io.mosip.kernel.auth.dto.otp.OtpUser;
import io.mosip.kernel.auth.dto.otp.idrepo.ResponseDTO;
import io.mosip.kernel.auth.exception.AuthManagerException;
import io.mosip.kernel.auth.exception.AuthManagerServiceException;
import io.mosip.kernel.auth.service.TokenGenerationService;
import io.mosip.kernel.auth.service.UinService;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author M1049825
 *
 */

@Component
public class UinServiceImpl implements UinService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	MosipEnvironment env;
	
	@Autowired
	Environment en;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private TokenGenerationService tokenService;
	
	public static final String MOSIP_NOTIFICATIONTYPE = "mosip.notificationtype";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.auth.service.UinService#getDetailsFromUin(io.mosip.kernel
	 * .auth.entities.otp.OtpUser)
	 */
	@Override
	public MosipUserDto getDetailsFromUin(OtpUser otpUser) throws Exception {
		String token=null;
		MosipUserDto mosipDto = null;
		ResponseDTO idResponse = null;
		
		Map<String, String> uriParams = new HashMap<String, String>();
		try {
			token = tokenService.getUINBasedToken();
		} catch (Exception e) {
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),e.getMessage(),e);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.set(AuthConstant.COOKIE, AuthConstant.AUTH_HEADER+token);
		uriParams.put(AuthConstant.APPTYPE_UIN.toLowerCase(), otpUser.getUserId());
		ResponseEntity<String> response = null;
		String url = UriComponentsBuilder.fromHttpUrl(env.getUinGetDetailsUrl()).buildAndExpand(uriParams).toUriString();
		try
		{
		response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<Object>(headers),
				String.class);
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
				idResponse = mapper.readValue(mapper.writeValueAsString(responseObject.getResponse()),
						ResponseDTO.class);
				
			} catch (Exception e) {
				throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), e.getMessage(),e);
			}
		}
			Map<String,String> res = (LinkedHashMap<String, String>) idResponse.getIdentity();
			if(res!=null)
			{
				mosipDto = new MosipUserDto();
				mosipDto.setUserId(otpUser.getUserId());
				validate(res,otpUser.getOtpChannel());
				if(res.get("phone")!=null)
				{
					mosipDto.setMobile((String) res.get("phone"));
				}
				if(res.get("email")!=null)
				{
					mosipDto.setMail(res.get("email"));
				}
			}	
		}catch (HttpClientErrorException | HttpServerErrorException ex) {
			List<ServiceError> validationErrorsList = ExceptionUtils.getServiceErrorList(ex.getResponseBodyAsString());

			if (ex.getRawStatusCode() == 401) {
				if (!validationErrorsList.isEmpty()) {
					throw new AuthNException(validationErrorsList);
				} else {
					throw new BadCredentialsException("Authentication failed from UIN services "+ex.getResponseBodyAsString());
				}
			}
			if (ex.getRawStatusCode() == 403) {
				if (!validationErrorsList.isEmpty()) {
					throw new AuthZException(validationErrorsList);
				} else {
					throw new AccessDeniedException("Access denied from UIN services");
				}
			}
			if (!validationErrorsList.isEmpty()) {
				throw new AuthManagerServiceException(validationErrorsList);
			} else {
				throw new AuthManagerException(AuthErrorCode.CLIENT_ERROR.getErrorCode(),
						AuthErrorCode.CLIENT_ERROR.getErrorMessage(),ex);
			}
		}
		
		return mosipDto;
	}
	
	@Override
	public MosipUserDto getDetailsForValidateOtp(String uin) throws Exception {
		String token=null;
		MosipUserDto mosipDto = null;
		ResponseDTO idResponse = null;
		
		Map<String, String> uriParams = new HashMap<String, String>();
		try {
			token = tokenService.getUINBasedToken();
		} catch (Exception e) {
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),e.getMessage());
		}
		HttpHeaders headers = new HttpHeaders();
		headers.set(AuthConstant.COOKIE, AuthConstant.AUTH_HEADER+token);
		uriParams.put(AuthConstant.APPTYPE_UIN.toLowerCase(), uin);
		ResponseEntity<String> response = null;
		String url = UriComponentsBuilder.fromHttpUrl(env.getUinGetDetailsUrl()).buildAndExpand(uriParams).toUriString();
		try
		{
		response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<Object>(headers),
				String.class);
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			String responseBody = response.getBody();
			List<ServiceError> validationErrorsList = null;
				validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);
				Optional<ServiceError> service = validationErrorsList.stream().filter(a->(a.getErrorCode().equals("IDR-IDC-002") || a.getErrorCode().equals("IDR-IDS-002"))).findFirst();
				if(service.isPresent())
				{
					throw new AuthManagerException(AuthErrorCode.USER_VALIDATION_ERROR.getErrorCode(),
							AuthErrorCode.USER_VALIDATION_ERROR.getErrorMessage());
				}
			if (!validationErrorsList.isEmpty()) {
				throw new AuthManagerServiceException(validationErrorsList);
			}
			ResponseWrapper<?> responseObject;
			try {
				responseObject = mapper.readValue(response.getBody(), ResponseWrapper.class);
				idResponse = mapper.readValue(mapper.writeValueAsString(responseObject.getResponse()),
						ResponseDTO.class);
				
			} catch (Exception e) {
				throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), e.getMessage(),e);
			}
		}
			Map<String,String> res = (LinkedHashMap<String, String>) idResponse.getIdentity();
			if(res!=null)
			{
				mosipDto = new MosipUserDto();
				mosipDto.setUserId(uin);
				validate(res,null);
				if(res.get("phone")!=null)
				{
					mosipDto.setMobile((String) res.get("phone"));
				}
				if(res.get("email")!=null)
				{
					mosipDto.setMail(res.get("email"));
				}
			}	
		}catch (HttpClientErrorException | HttpServerErrorException ex) {
			List<ServiceError> validationErrorsList = ExceptionUtils.getServiceErrorList(ex.getResponseBodyAsString());

			if (ex.getRawStatusCode() == 401) {
				if (!validationErrorsList.isEmpty()) {
					throw new AuthNException(validationErrorsList);
				} else {
					throw new BadCredentialsException("Authentication failed from UIN services "+ex.getResponseBodyAsString());
				}
			}
			if (ex.getRawStatusCode() == 403) {
				if (!validationErrorsList.isEmpty()) {
					throw new AuthZException(validationErrorsList);
				} else {
					throw new AccessDeniedException("Access denied from UIN services");
				}
			}
			if (!validationErrorsList.isEmpty()) {
				throw new AuthManagerServiceException(validationErrorsList);
			} else {
				throw new AuthManagerException(AuthErrorCode.CLIENT_ERROR.getErrorCode(),
						AuthErrorCode.CLIENT_ERROR.getErrorMessage() + ex.getMessage());
			}
		}
		
		return mosipDto;
	}
	

	private void validate(Map<String, String> res, List<String> channelList) {
		String notficationType = en.getProperty(MOSIP_NOTIFICATIONTYPE);
		String phone,email;
		String[] notificationArray = notficationType.split("[\\|\\s]+");
		List<String> notifyList = Arrays.asList(notificationArray);		
		phone =(String) res.get("phone");
		email = (String) res.get("email");
		validateConfigChannel(notifyList,channelList);
		if((StringUtils.isBlank(phone) && StringUtils.isBlank(email)) && (channelList.contains(AuthConstant.PHONE) && channelList.contains(AuthConstant.EMAIL)))
		{
			throw new AuthManagerException(OTPErrorCode.EMAILPHONENOTREGISTERED.getErrorCode(),OTPErrorCode.EMAILPHONENOTREGISTERED.getErrorMessage());
		}
		else if(StringUtils.isBlank(phone) && channelList.contains(AuthConstant.PHONE))
		{
			throw new AuthManagerException(OTPErrorCode.PHONENOTREGISTERED.getErrorCode(),OTPErrorCode.PHONENOTREGISTERED.getErrorMessage());
		}
		else if(StringUtils.isBlank(email) && channelList.contains(AuthConstant.EMAIL))
		{
			throw new AuthManagerException(OTPErrorCode.EMAILNOTREGISTERED.getErrorCode(),OTPErrorCode.EMAILNOTREGISTERED.getErrorMessage());
		}
		
	}

	private void validateConfigChannel(List<String> notifyList, List<String> channelList) {
		if((!notifyList.contains(AuthConstant.SMS_NOTIFYTYPE) && !notifyList.contains(AuthConstant.EMAIL_NOTIFYTYPE)) && (channelList.contains(AuthConstant.PHONE) && channelList.contains(AuthConstant.EMAIL)))
		{
			throw new AuthManagerException(OTPErrorCode.EMAILSMSNOTCONFIGURED.getErrorCode(),OTPErrorCode.EMAILSMSNOTCONFIGURED.getErrorMessage());
		}
		else if(!notifyList.contains(AuthConstant.SMS_NOTIFYTYPE) && channelList.contains(AuthConstant.PHONE))
		{
			throw new AuthManagerException(OTPErrorCode.SMSNOTCONFIGURED.getErrorCode(),OTPErrorCode.SMSNOTCONFIGURED.getErrorMessage());
		}
		else if(!notifyList.contains(AuthConstant.EMAIL_NOTIFYTYPE) && channelList.contains(AuthConstant.EMAIL))
		{
			throw new AuthManagerException(OTPErrorCode.SMSNOTCONFIGURED.getErrorCode(),OTPErrorCode.SMSNOTCONFIGURED.getErrorMessage());
		}
		
		
	}

}
