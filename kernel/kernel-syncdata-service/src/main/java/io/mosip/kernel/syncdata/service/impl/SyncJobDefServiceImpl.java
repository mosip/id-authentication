package io.mosip.kernel.syncdata.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.syncdata.constant.AdminServiceErrorCode;
import io.mosip.kernel.syncdata.dto.SyncJobDefDto;
import io.mosip.kernel.syncdata.entity.SyncJobDef;
import io.mosip.kernel.syncdata.exception.AdminServiceException;
import io.mosip.kernel.syncdata.service.SyncJobDefService;
import io.mosip.kernel.syncdata.syncjob.repository.SyncJobDefRepository;
import io.mosip.kernel.syncdata.utils.MapperUtils;

/**
 * This class contains the business logic for CRUD opertaion.
 *
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Service
public class SyncJobDefServiceImpl implements SyncJobDefService {

	@Autowired
	ObjectMapper objectMapper;

//	@Autowired
//	RestTemplate restTemplate;
//
//	@Value("${mosip.kernel.syncdata.syncjob-base-url}")
//	private String baseUri;
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * io.mosip.kernel.syncdata.service.SyncJobDefService#getSyncJobDefDetails(java.
//	 * time.LocalDateTime, java.time.LocalDateTime)
//	 */
//	@Override
//	public List<SyncJobDefDto> getSyncJobDefDetails(LocalDateTime lastUpdatedTime, LocalDateTime currentTimeStamp) {
//		ResponseEntity<String> response = null;
//
//		try {
//			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUri)
//					// Add query parameter
//					.queryParam("lastupdatedtimestamp", DateUtils.formatToISOString(lastUpdatedTime));
//
//			response = restTemplate.getForEntity(builder.toUriString(), String.class);
//		} catch (HttpServerErrorException | HttpClientErrorException ex) {
//			List<ServiceError> validationErrorsList = ExceptionUtils.getServiceErrorList(ex.getResponseBodyAsString());
//
//			if (ex.getRawStatusCode() == 401) {
//				if (!validationErrorsList.isEmpty()) {
//					throw new AuthNException(validationErrorsList);
//				} else {
//					throw new BadCredentialsException("Authentication failed from AdminService");
//				}
//			}
//			if (ex.getRawStatusCode() == 403) {
//				if (!validationErrorsList.isEmpty()) {
//					throw new AuthZException(validationErrorsList);
//				} else {
//					throw new AccessDeniedException("Access denied from AdminService");
//				}
//			}
//			throw new SyncDataServiceException(MasterDataErrorCode.SYNC_JOB_DEF_FETCH_EXCEPTION.getErrorCode(),
//					MasterDataErrorCode.SYNC_JOB_DEF_FETCH_EXCEPTION.getErrorMessage() + ex.getMessage());
//		}
//		String responseBody = response.getBody();
//		return getSyncJobDefDetail(responseBody);
//	}
//
//	/**
//	 * Gets the sync job def detail.
//	 *
//	 * @param responseBody the response body
//	 * @return the sync job def detail
//	 */
//	private List<SyncJobDefDto> getSyncJobDefDetail(String responseBody) {
//		List<SyncJobDefDto> syncJobDefDtos = null;
//		List<ServiceError> validationErrorsList = null;
//		validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);
//
//		if (!validationErrorsList.isEmpty()) {
//			throw new SyncServiceException(validationErrorsList);
//		}
//		ResponseWrapper<SyncJobDefResponseDto> responseObject = null;
//		try {
//
//			responseObject = objectMapper.readValue(responseBody,
//					new TypeReference<ResponseWrapper<SyncJobDefResponseDto>>() {
//					});
//			if (responseObject.getResponse() != null) {
//				syncJobDefDtos = responseObject.getResponse().getSyncJobDefinitions();
//			}
//
//		} catch (IOException | NullPointerException exception) {
//			throw new ParseResponseException(MasterDataErrorCode.SYNC_JOB_DEF_PARSE_EXCEPTION.getErrorCode(),
//					MasterDataErrorCode.SYNC_JOB_DEF_PARSE_EXCEPTION.getErrorMessage() + exception.getMessage(),
//					exception);
//		}
//
//		return syncJobDefDtos;
//	}

@Autowired
private SyncJobDefRepository syncJobDefRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.syncdata.service.SyncJobDefService#getSyncJobDefDetails(java.
	 * time.LocalDateTime, java.time.LocalDateTime)
	 */
	@Override
	public List<SyncJobDefDto> getSyncJobDefDetails(LocalDateTime lastUpdatedTime, LocalDateTime currentTimeStamp) {

		List<SyncJobDefDto> syncJobDefDtos = null;
		List<SyncJobDef> syncJobDefs = null;
		if (lastUpdatedTime == null) {
			lastUpdatedTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
		}
		try {
			syncJobDefs = syncJobDefRepository.findLatestByLastUpdatedTimeAndCurrentTimeStamp(lastUpdatedTime,
					currentTimeStamp);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new AdminServiceException(AdminServiceErrorCode.SYNC_JOB_DEF_FETCH_EXCEPTION.getErrorCode(),
					AdminServiceErrorCode.SYNC_JOB_DEF_FETCH_EXCEPTION.getErrorMessage());
		}
		if (syncJobDefs != null && !syncJobDefs.isEmpty()) {
			syncJobDefDtos = MapperUtils.mapAll(syncJobDefs, SyncJobDefDto.class);
		}
		return syncJobDefDtos;
	}

}
