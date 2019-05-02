package io.mosip.kernel.syncdata.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auth.adapter.exception.AuthNException;
import io.mosip.kernel.auth.adapter.exception.AuthZException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.syncdata.constant.RolesErrorCode;
import io.mosip.kernel.syncdata.constant.UserDetailsErrorCode;
import io.mosip.kernel.syncdata.dto.response.RolesResponseDto;
import io.mosip.kernel.syncdata.exception.ParseResponseException;
import io.mosip.kernel.syncdata.exception.SyncDataServiceException;
import io.mosip.kernel.syncdata.exception.SyncServiceException;
import io.mosip.kernel.syncdata.service.SyncRolesService;

/**
 * This class handles fetching of everey roles that is in the server. The flow
 * is given as follows SYNC - AUTH SERVICE - AUTH SERVER
 * 
 * @author Srinivasan
 * @since 1.0.0
 * 
 */
@RefreshScope
@Service
public class SyncRolesServiceImpl implements SyncRolesService {

	/** restemplate instance. */
	@Autowired
	private RestTemplate restTemplate;

	/** Base end point read from property file. */
	@Value("${mosip.kernel.syncdata.auth-manager-base-uri}")
	private String authBaseUrl;

	/** The object mapper. */
	@Autowired
	private ObjectMapper objectMapper;

	/** all roles end-point read from properties file. */
	@Value("${mosip.kernel.syncdata.auth-manager-roles}")
	private String authServiceName;

	/** The sync data request id. */
	@Value("${mosip.kernel.syncdata.syncdata-request-id:SYNCDATA.REQUEST}")
	private String syncDataRequestId;

	/** The sync data version id. */
	@Value("${mosip.kernel.syncdata.syncdata-version-id:v1.0}")
	private String syncDataVersionId;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.syncdata.service.SyncRolesService#getAllRoles()
	 */
	@Override
	public RolesResponseDto getAllRoles() {

		ResponseEntity<String> response = null;
		try {

			StringBuilder uriBuilder = new StringBuilder();
			uriBuilder.append(authBaseUrl).append(authServiceName);
			HttpEntity<RequestWrapper<?>> httpRequest = getHttpRequest();
			response = restTemplate.exchange(uriBuilder.toString() + "/registrationclient", HttpMethod.GET, httpRequest,
					String.class);
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
			throw new SyncDataServiceException(RolesErrorCode.ROLES_FETCH_EXCEPTION.getErrorCode(),
					RolesErrorCode.ROLES_FETCH_EXCEPTION.getErrorMessage());
		}

		return getRolesFromResponse(response);

	}

	/**
	 * Gets the http request.
	 *
	 * @return {@link HttpEntity}
	 */
	private HttpEntity<RequestWrapper<?>> getHttpRequest() {
		RequestWrapper<?> requestWrapper = new RequestWrapper<>();
		requestWrapper.setId(syncDataRequestId);
		requestWrapper.setVersion(syncDataVersionId);
		HttpHeaders rolesHttpHeaders = new HttpHeaders();
		rolesHttpHeaders.setContentType(MediaType.APPLICATION_JSON);

		return new HttpEntity<>(requestWrapper, rolesHttpHeaders);

	}

	/**
	 * Gets the roles from response.
	 *
	 * @param response the response
	 * @return {@link RolesResponseDto}
	 */
	private RolesResponseDto getRolesFromResponse(ResponseEntity<String> response) {
		String responseBody = response.getBody();
		List<ServiceError> validationErrorsList = null;
		RolesResponseDto rolesDtos = null;
		validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);
		if (!validationErrorsList.isEmpty()) {
			throw new SyncServiceException(validationErrorsList);
		}
		ResponseWrapper<?> responseObject = null;
		try {
			responseObject = objectMapper.readValue(response.getBody(), ResponseWrapper.class);
			rolesDtos = objectMapper.readValue(objectMapper.writeValueAsString(responseObject.getResponse()),
					RolesResponseDto.class);
		} catch (IOException | NullPointerException exception) {
			throw new ParseResponseException(UserDetailsErrorCode.USER_DETAILS_PARSE_ERROR.getErrorCode(),
					UserDetailsErrorCode.USER_DETAILS_PARSE_ERROR.getErrorMessage() + exception.getMessage(),
					exception);
		}
		return rolesDtos;
	}

}
