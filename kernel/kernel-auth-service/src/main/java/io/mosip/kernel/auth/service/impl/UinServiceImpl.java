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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
	private ObjectMapper mapper;
	
	@Autowired
	private TokenGenerationService tokenService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.auth.service.UinService#getDetailsFromUin(io.mosip.kernel
	 * .auth.entities.otp.OtpUser)
	 */
	@Override
	public MosipUserDto getDetailsFromUin(String uin) throws Exception {
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
			if (!validationErrorsList.isEmpty()) {
				throw new AuthManagerServiceException(validationErrorsList);
			}
			ResponseWrapper<?> responseObject;
			try {
				responseObject = mapper.readValue(response.getBody(), ResponseWrapper.class);
				idResponse = mapper.readValue(mapper.writeValueAsString(responseObject.getResponse()),
						ResponseDTO.class);
				
			} catch (Exception e) {
				throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), e.getMessage());
			}
		}
			Map<String,String> res = (LinkedHashMap<String, String>) idResponse.getIdentity();
			if(res!=null)
			{
				mosipDto = new MosipUserDto();
				mosipDto.setUserId(uin);
				validate(res);
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
				throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), e.getMessage());
			}
		}
			Map<String,String> res = (LinkedHashMap<String, String>) idResponse.getIdentity();
			if(res!=null)
			{
				mosipDto = new MosipUserDto();
				mosipDto.setUserId(uin);
				validate(res);
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
	

	private void validate(Map<String, String> res) {
		if((String) res.get("phone")==null && (String) res.get("email")==null)
		{
			throw new AuthManagerException(OTPErrorCode.EMAILPHONENOTREGISTERED.getErrorCode(),OTPErrorCode.EMAILPHONENOTREGISTERED.getErrorMessage());
		}
		else if(res.get("phone")==null)
		{
			throw new AuthManagerException(OTPErrorCode.PHONENOTREGISTERED.getErrorCode(),OTPErrorCode.PHONENOTREGISTERED.getErrorMessage());
		}
		else if(res.get("email")==null)
		{
			throw new AuthManagerException(OTPErrorCode.EMAILNOTREGISTERED.getErrorCode(),OTPErrorCode.EMAILNOTREGISTERED.getErrorMessage());
		}
		
	}

}
