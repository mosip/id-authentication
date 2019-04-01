package io.mosip.kernel.syncdata.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.syncdata.constant.UserDetailsErrorCode;
import io.mosip.kernel.syncdata.dto.RegistrationCenterUserDto;
import io.mosip.kernel.syncdata.dto.SyncUserDetailDto;
import io.mosip.kernel.syncdata.dto.UserDetailMapDto;
import io.mosip.kernel.syncdata.dto.UserDetailRequestDto;
import io.mosip.kernel.syncdata.dto.response.RegistrationCenterUserResponseDto;
import io.mosip.kernel.syncdata.dto.response.UserDetailResponseDto;
import io.mosip.kernel.syncdata.exception.AuthManagerServiceException;
import io.mosip.kernel.syncdata.exception.ParseResponseException;
import io.mosip.kernel.syncdata.exception.SyncDataServiceException;
import io.mosip.kernel.syncdata.service.RegistrationCenterUserService;
import io.mosip.kernel.syncdata.service.SyncUserDetailsService;
import io.mosip.kernel.syncdata.utils.MapperUtils;

/**
 * This class will fetch all user details from the LDAP server through
 * auth-service
 * 
 * @author Srinivasan
 * @author Megha Tanga
 * @since 1.0.0
 */
@RefreshScope
@Service
public class SyncUserDetailsServiceImpl implements SyncUserDetailsService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	RegistrationCenterUserService registrationCenterUserService;

	@Value("${mosip.kernel.syncdata.auth-manager-base-uri:https://dev.mosip.io/authmanager/v1.0}")
	private String authUserDetailsBaseUri;

	@Value("${mosip.kernel.syncdata.auth-user-details:/userdetails}")
	private String authUserDetailsUri;

	@Value("${mosip.kernel.syncdata.syncdata-request-id:SYNCDATA.REQUEST}")
	private String syncDataRequestId;

	@Value("${mosip.kernel.syncdata.syncdata-version-id:v1.0}")
	private String syncDataVersionId;

	/**
	 * 
	 */
	@Override
	public SyncUserDetailDto getAllUserDetail(String regId) {
		StringBuilder userDetailsUri = new StringBuilder();
		userDetailsUri.append(authUserDetailsBaseUri).append(authUserDetailsUri);
		UserDetailResponseDto data = null;
		SyncUserDetailDto syncUserDetailDto = null;
		RegistrationCenterUserResponseDto registrationCenterResponseDto = registrationCenterUserService
				.getUsersBasedOnRegistrationCenterId(regId);
		List<RegistrationCenterUserDto> registrationCenterUserDtos = registrationCenterResponseDto
				.getRegistrationCenterUsers();
		List<String> userIds = registrationCenterUserDtos.stream().map(RegistrationCenterUserDto::getUserId)
				.collect(Collectors.toList());
		RequestWrapper<UserDetailRequestDto> requestWrapper = new RequestWrapper<>();
		requestWrapper.setId(syncDataRequestId);
		requestWrapper.setVersion(syncDataVersionId);
		HttpHeaders syncDataRequestHeaders = new HttpHeaders();
		syncDataRequestHeaders.setContentType(MediaType.APPLICATION_JSON);
		UserDetailRequestDto userDetailsDto = new UserDetailRequestDto();
		userDetailsDto.setUserDetails(userIds);
		requestWrapper.setRequest(userDetailsDto);
		HttpEntity<RequestWrapper<?>> userDetailRequestEntity = new HttpEntity<>(requestWrapper,
				syncDataRequestHeaders);

		try {

			ResponseEntity<String> response = restTemplate.postForEntity(
					userDetailsUri.toString() + "/registrationclient", userDetailRequestEntity, String.class);
			/*
			 * ResponseEntity<String> response = restTemplate.postForEntity(
			 * "https://dev.mosip.io/authmanager/userdetails/registrationclient",
			 * userDetailRequestEntity, String.class);
			 */

			String responseBody = response.getBody();
			List<ServiceError> validationErrorsList = null;
			validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);

			if (!validationErrorsList.isEmpty()) {
				throw new AuthManagerServiceException(validationErrorsList);
			}
			ResponseWrapper<UserDetailResponseDto> responseObject = null;
			try {
				responseObject = objectMapper.readValue(response.getBody(),
						new TypeReference<ResponseWrapper<UserDetailResponseDto>>() {
						});
				data = responseObject.getResponse();
			} catch (IOException | NullPointerException exception) {
				throw new ParseResponseException(UserDetailsErrorCode.USER_DETAILS_PARSE_ERROR.getErrorCode(),
						UserDetailsErrorCode.USER_DETAILS_PARSE_ERROR.getErrorMessage() + exception.getMessage(),
						exception);
			}

			if (data != null && data.getMosipUserDtoList() != null) {
				List<UserDetailMapDto> userDetails = MapperUtils
						.mapUserDetailsToUserDetailMap(data.getMosipUserDtoList());
				syncUserDetailDto = new SyncUserDetailDto();
				syncUserDetailDto.setUserDetails(userDetails);
			}
			return syncUserDetailDto;

		} catch (HttpServerErrorException | HttpClientErrorException e) {
			System.out.println(e.getResponseBodyAsString());
			throw new SyncDataServiceException(UserDetailsErrorCode.USER_DETAILS_FETCH_EXCEPTION.getErrorCode(),
					UserDetailsErrorCode.USER_DETAILS_FETCH_EXCEPTION.getErrorMessage(), e);
		}

	}

}
