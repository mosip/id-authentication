package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_MASTER_SYNC;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.MasterSyncDao;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.mastersync.LocationDto;
import io.mosip.registration.dto.mastersync.MasterDataResponseDto;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.mastersync.MasterLocation;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.MasterSyncService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

/**
 * Service class to sync master data from server to client
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
@Service
public class MasterSyncServiceImpl implements MasterSyncService {

	/** Object for masterSyncDao class. */
	@Autowired
	private MasterSyncDao masterSyncDao;

	@Autowired
	ServiceDelegateUtil serviceDelegateUtil;

	/** Object for Logger. */
	private static final Logger LOGGER = AppConfig.getLogger(MasterSyncServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.MasterSyncService#getMasterSync(java.lang.
	 * String)
	 */
	@Override
	public ResponseDTO getMasterSync(String masterSyncDtls) {

		ResponseDTO responseDTO = null;
		String resoponse = null;

		SuccessResponseDTO sucessResponse = new SuccessResponseDTO();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		SyncControl masterSyncDetails;

		if (!RegistrationAppHealthCheckUtil.isNetworkAvailable()) {

			responseDTO = buildErrorRespone(RegistrationConstants.MASTER_SYNC_OFFLINE_FAILURE_MSG_CODE,
					RegistrationConstants.MASTER_SYNC_OFFLINE_FAILURE_MSG);
			return responseDTO;

		}

		LOGGER.debug(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
				"Fetching the last sync details from databse ended");
		try {

			// getting Last Sync date from Data from sync table
			masterSyncDetails = masterSyncDao.syncJobDetails(masterSyncDtls);

			Timestamp lastSyncTime = masterSyncDetails.getLastSyncDtimes();

			// Converting Time Stamp to LocalDateTime
			LocalDateTime masterLastSyncTime = LocalDateTime.ofInstant(lastSyncTime.toInstant(), ZoneOffset.ofHours(0));

			// Getting machineID from data base
			String machineId = masterSyncDetails.getMachineId();

			LOGGER.debug(LOG_REG_MASTER_SYNC, APPLICATION_NAME, "registrationCenterId" + "===>" + machineId,
					"lastSyncTime" + "===>" + lastSyncTime);

			Object masterSyncJson = getMasterSyncJson(machineId, masterLastSyncTime);

			if (null != masterSyncJson) {

				LOGGER.debug(RegistrationConstants.MASTER_SYNC, APPLICATION_NAME, "MASTER-SYNC-RESTFUL_SERVICE-ENDS",
						"master sync json ======>" + masterSyncJson.toString());

				LOGGER.debug(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
						"MASTER-SYNC-RESTFUL_SERVICE-BEGINE");

				// Mapping json object to respective dto's
				MasterDataResponseDto masterSyncDto = objectMapper.readValue(masterSyncJson.toString(),
						MasterDataResponseDto.class);

				resoponse = masterSyncDao.save(masterSyncDto);

				LOGGER.debug(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, "MASTER-SYNC-RESTFUL_SERVICE-ENDS");

				sucessResponse.setCode(RegistrationConstants.MASTER_SYNC_SUCESS_MSG_CODE);
				sucessResponse.setInfoType(RegistrationConstants.ALERT_INFORMATION);
				sucessResponse.setMessage(RegistrationConstants.MASTER_SYNC_SUCCESS);
				responseDTO = new ResponseDTO();
				responseDTO.setSuccessResponseDTO(sucessResponse);

			} else {

				responseDTO = buildErrorRespone(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_CODE,
						RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);
			}

		} catch (RegBaseUncheckedException | RegBaseCheckedException regBaseUncheckedException) {

			LOGGER.error(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					regBaseUncheckedException.getMessage() + resoponse);

			responseDTO = buildErrorRespone(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_CODE,
					RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);

		} catch (RuntimeException | IOException runtimeException) {

			LOGGER.error(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + resoponse);

			responseDTO = buildErrorRespone(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_CODE,
					RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);

		} 
		
		return responseDTO;
	}

	/**
	 * Gets the master sync json.
	 *
	 * @param machineId    the machine id
	 * @param lastSyncTime the last sync time
	 * @return the master sync json
	 * @throws RegBaseCheckedException the reg base checked exception
	 */
	private Object getMasterSyncJson(String machineId, LocalDateTime lastSyncTime) throws RegBaseCheckedException {

		Object response = null;

		// Setting uri Variables

		Map<String, String> requestParamMap = new LinkedHashMap<>();
		requestParamMap.put("machineId", machineId);
		requestParamMap.put("lastUpdated", lastSyncTime.toString());

		try {
			response = serviceDelegateUtil.get(RegistrationConstants.MASTER_VALIDATOR_SERVICE_NAME, requestParamMap);
		} catch (HttpClientErrorException httpClientErrorException) {
			LOGGER.error(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					httpClientErrorException.getRawStatusCode() + "Http error while pulling json from server");
			throw new RegBaseCheckedException(Integer.toString(httpClientErrorException.getRawStatusCode()),
					httpClientErrorException.getStatusText());
		} catch (SocketTimeoutException socketTimeoutException) {
			LOGGER.error(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					socketTimeoutException.getMessage() + "Http error while pulling json from server");
			throw new RegBaseCheckedException(socketTimeoutException.getMessage(),
					socketTimeoutException.getLocalizedMessage());
		}

		return response;
	}

	/**
	 * Builds the error respone.
	 *
	 * @param errorCode the error code
	 * @param message   the message
	 * @return the response DTO
	 */
	private ResponseDTO buildErrorRespone(final String errorCode, final String message) {

		ResponseDTO response = new ResponseDTO();

		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		/* Error response */
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setCode(errorCode);
		errorResponse.setInfoType(RegistrationConstants.ALERT_ERROR);
		errorResponse.setMessage(message);

		errorResponses.add(errorResponse);

		/* Adding list of error responses to response */
		response.setErrorResponseDTOs(errorResponses);

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.MasterSyncService#findLocationByHierarchyCode(
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public List<LocationDto> findLocationByHierarchyCode(String hierarchyCode, String langCode) {

		List<LocationDto> locationDto = new ArrayList<>();

		List<MasterLocation> masterLocation = masterSyncDao.findLocationByLangCode(hierarchyCode, langCode);

		for (MasterLocation masLocation : masterLocation) {
			LocationDto location = new LocationDto();
			location.setCode(masLocation.getCode());
			location.setHierarchyName(masLocation.getHierarchyName());
			location.setName(masLocation.getName());
			location.setLanguageCode(masLocation.getLanguageCode());
			locationDto.add(location);
		}

		return locationDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.MasterSyncService#findProvianceByHierarchyCode(
	 * java.lang.String)
	 */
	@Override
	public List<LocationDto> findProvianceByHierarchyCode(String code) {

		List<LocationDto> locationDto = new ArrayList<>();

		List<MasterLocation> masterLocation = masterSyncDao.findLocationByParentLocCode(code);

		for (MasterLocation masLocation : masterLocation) {
			LocationDto location = new LocationDto();
			location.setCode(masLocation.getCode());
			location.setHierarchyName(masLocation.getHierarchyName());
			location.setName(masLocation.getName());
			location.setLanguageCode(masLocation.getLanguageCode());
			locationDto.add(location);
		}

		return locationDto;
	}
}
