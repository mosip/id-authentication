package io.mosip.admin.accountmgmt.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.admin.accountmgmt.constant.AccountManagementErrorCode;
import io.mosip.admin.accountmgmt.dto.UnBlockResponseDto;
import io.mosip.admin.accountmgmt.dto.UserNameDto;
import io.mosip.admin.accountmgmt.exception.AccountManagementServiceException;
import io.mosip.admin.accountmgmt.exception.AccountServiceException;
import io.mosip.admin.accountmgmt.service.AccountManagementService;
import io.mosip.kernel.auth.adapter.exception.AuthNException;
import io.mosip.kernel.auth.adapter.exception.AuthZException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.signatureutil.exception.ParseResponseException;

/**
 * The Class AccountManagementServiceImpl.
 * 
 * @author Srinivasan
 * @since 1.0.0
 */
@Service
public class AccountManagementServiceImpl implements AccountManagementService {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${mosip.admin.accountmgmt.auth-manager-base-uri}")
	private String authManagerBaseUrl;

	@Value("${mosip.admin.accountmgmt.user-name-url}")
	private String userNameUrl;
	
	@Value("${mosip.admin.accountmgmt.unblock-url}")
	private String unBlockUrl;

	@Autowired
	private ObjectMapper objectMapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.admin.accountmgmt.service.AccountManagementService#getUserName(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public UserNameDto getUserName(String userId) {
		String response = null;
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(authManagerBaseUrl).append(userNameUrl + "registrationclient/").append(userId);
		response = callAuthManagerService(urlBuilder.toString());
		UserNameDto userNameDto = getUserDetailFromResponse(response);

		return userNameDto;
	}

	private String callAuthManagerService(String url) {
		String response = null;
		try {
			response = restTemplate.getForObject(url, String.class);
		} catch (HttpServerErrorException | HttpClientErrorException ex) {
			List<ServiceError> validationErrorsList = ExceptionUtils.getServiceErrorList(ex.getResponseBodyAsString());

			if (ex.getRawStatusCode() == 401) {
				if (!validationErrorsList.isEmpty()) {
					throw new AuthNException(validationErrorsList);
				} else {
					throw new BadCredentialsException("Authentication failed from AuthManager");
				}
			}
			if (ex.getRawStatusCode() == 403) {
				if (!validationErrorsList.isEmpty()) {
					throw new AuthZException(validationErrorsList);
				} else {
					throw new AccessDeniedException("Access denied from AuthManager");
				}
			}
			throw new AccountManagementServiceException(
					AccountManagementErrorCode.REST_SERVICE_EXCEPTION.getErrorCode(),
					AccountManagementErrorCode.REST_SERVICE_EXCEPTION.getErrorMessage(), ex);
		}

		return response;
	}

	private UserNameDto getUserDetailFromResponse(String responseBody) {
		List<ServiceError> validationErrorsList = null;
		validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);
		UserNameDto userNameDto = null;
		if (!validationErrorsList.isEmpty()) {
			throw new AccountServiceException(validationErrorsList);
		}
		ResponseWrapper<UserNameDto> responseObject = null;
		try {

			responseObject = objectMapper.readValue(responseBody, new TypeReference<ResponseWrapper<UserNameDto>>() {
			});
			userNameDto = responseObject.getResponse();
		} catch (IOException | NullPointerException exception) {
			throw new ParseResponseException(AccountManagementErrorCode.PARSE_EXCEPTION.getErrorCode(),
					AccountManagementErrorCode.PARSE_EXCEPTION.getErrorMessage() + exception.getMessage(), exception);
		}

		return userNameDto;
	}

	@Override
	public UnBlockResponseDto unBlockUserName(String userId) {
		String response=null;
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(authManagerBaseUrl).append(unBlockUrl + "registrationclient/").append(userId);
		response = callAuthManagerService(urlBuilder.toString());
		UnBlockResponseDto unBlockResponseDto=getSuccessResponse(response);
		return unBlockResponseDto;
	}
	
	private UnBlockResponseDto getSuccessResponse(String responseBody) {
		List<ServiceError> validationErrorsList = null;
		validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);
		UnBlockResponseDto unBlockResponseDto = null;
		if (!validationErrorsList.isEmpty()) {
			throw new AccountServiceException(validationErrorsList);
		}
		ResponseWrapper<UnBlockResponseDto> responseObject = null;
		try {

			responseObject = objectMapper.readValue(responseBody, new TypeReference<ResponseWrapper<UnBlockResponseDto>>() {
			});
			unBlockResponseDto = responseObject.getResponse();
		} catch (IOException | NullPointerException exception) {
			throw new ParseResponseException(AccountManagementErrorCode.PARSE_EXCEPTION.getErrorCode(),
					AccountManagementErrorCode.PARSE_EXCEPTION.getErrorMessage() + exception.getMessage(), exception);
		}

		return unBlockResponseDto;
	}

}
