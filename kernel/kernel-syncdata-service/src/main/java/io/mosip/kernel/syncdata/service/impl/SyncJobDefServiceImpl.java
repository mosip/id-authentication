package io.mosip.kernel.syncdata.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auth.adapter.exception.AuthNException;
import io.mosip.kernel.auth.adapter.exception.AuthZException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.signatureutil.exception.ParseResponseException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.syncdata.constant.MasterDataErrorCode;
import io.mosip.kernel.syncdata.dto.SyncJobDefDto;
import io.mosip.kernel.syncdata.dto.response.SyncJobDefResponseDto;
import io.mosip.kernel.syncdata.exception.SyncDataServiceException;
import io.mosip.kernel.syncdata.exception.SyncServiceException;
import io.mosip.kernel.syncdata.service.SyncJobDefService;

/**
 * This class contains the business logic for CRUD opertaion.
 *
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Service
public class SyncJobDefServiceImpl implements SyncJobDefService {

	/** The rest template. */
	@Autowired
	RestTemplate restTemplate;

	/** The object mapper. */
	@Autowired
	ObjectMapper objectMapper;

	/** The base uri. */
	@Value("${mosip.kernel.syncdata.syncjob-base-url}")
	private String baseUri;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.syncdata.service.SyncJobDefService#getSyncJobDefDetails(java.
	 * time.LocalDateTime, java.time.LocalDateTime)
	 */
	@Override
	public List<SyncJobDefDto> getSyncJobDefDetails(LocalDateTime lastUpdatedTime, LocalDateTime currentTimeStamp) {
		ResponseEntity<String> response = null;

		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUri)
					// Add query parameter
					.queryParam("lastupdatedtimestamp", DateUtils.formatToISOString(lastUpdatedTime));

			response = restTemplate.getForEntity(builder.toUriString(), String.class);
		} catch (HttpServerErrorException | HttpClientErrorException ex) {
			List<ServiceError> validationErrorsList = ExceptionUtils.getServiceErrorList(ex.getResponseBodyAsString());

			if (ex.getRawStatusCode() == 401) {
				if (!validationErrorsList.isEmpty()) {
					throw new AuthNException(validationErrorsList);
				} else {
					throw new BadCredentialsException("Authentication failed from AdminService");
				}
			}
			if (ex.getRawStatusCode() == 403) {
				if (!validationErrorsList.isEmpty()) {
					throw new AuthZException(validationErrorsList);
				} else {
					throw new AccessDeniedException("Access denied from AdminService");
				}
			}
			throw new SyncDataServiceException(MasterDataErrorCode.SYNC_JOB_DEF_FETCH_EXCEPTION.getErrorCode(),
					MasterDataErrorCode.SYNC_JOB_DEF_FETCH_EXCEPTION.getErrorMessage() + ex.getMessage());
		}
		String responseBody = response.getBody();
		return getSyncJobDefDetail(responseBody);
	}

	/**
	 * Gets the sync job def detail.
	 *
	 * @param responseBody the response body
	 * @return the sync job def detail
	 */
	private List<SyncJobDefDto> getSyncJobDefDetail(String responseBody) {
		List<SyncJobDefDto> syncJobDefDtos = null;
		List<ServiceError> validationErrorsList = null;
		validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);

		if (!validationErrorsList.isEmpty()) {
			throw new SyncServiceException(validationErrorsList);
		}
		ResponseWrapper<SyncJobDefResponseDto> responseObject = null;
		try {

			responseObject = objectMapper.readValue(responseBody,
					new TypeReference<ResponseWrapper<SyncJobDefResponseDto>>() {
					});
			if (responseObject.getResponse() != null) {
				syncJobDefDtos = responseObject.getResponse().getSyncJobDefinitions();
			}

		} catch (IOException | NullPointerException exception) {
			throw new ParseResponseException(MasterDataErrorCode.SYNC_JOB_DEF_PARSE_EXCEPTION.getErrorCode(),
					MasterDataErrorCode.SYNC_JOB_DEF_PARSE_EXCEPTION.getErrorMessage() + exception.getMessage(),
					exception);
		}

		return syncJobDefDtos;
	}

//@Autowired
//private SyncJobDefRepository syncJobDefRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.syncdata.service.SyncJobDefService#getSyncJobDefDetails(java.
	 * time.LocalDateTime, java.time.LocalDateTime)
	 */
//	@Override
//	public List<SyncJobDefDto> getSyncJobDefDetails(LocalDateTime lastUpdatedTime, LocalDateTime currentTimeStamp) {
//
//		List<SyncJobDefDto> syncJobDefDtos = null;
//		List<SyncJobDef> syncJobDefs = null;
//		if (lastUpdatedTime == null) {
//			lastUpdatedTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
//		}
//		try {
//			syncJobDefs = syncJobDefRepository.findLatestByLastUpdatedTimeAndCurrentTimeStamp(lastUpdatedTime,
//					currentTimeStamp);
//		} catch (DataAccessException | DataAccessLayerException e) {
//			throw new AdminServiceException(AdminServiceErrorCode.SYNC_JOB_DEF_FETCH_EXCEPTION.getErrorCode(),
//					AdminServiceErrorCode.SYNC_JOB_DEF_FETCH_EXCEPTION.getErrorMessage());
//		}
//		if (syncJobDefs != null && !syncJobDefs.isEmpty()) {
//			syncJobDefDtos = MapperUtils.mapAll(syncJobDefs, SyncJobDefDto.class);
//		}
//		return syncJobDefDtos;
//	}

}
