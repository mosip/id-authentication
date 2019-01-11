package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_USER_ONBOARD;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.UserOnboardDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.UserOnboardService;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
@Service
public class UserOnboardServiceImpl implements UserOnboardService {
	
	@Autowired
	private UserOnboardDAO userOnBoardDao;
	
	@Value("${USER_ON_BOARD_THRESHOLD_LIMIT}")
	private int UserOnBoardThresholdLimit;
	
	/**
	 * logger for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(UserOnboardServiceImpl.class);
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.UserOnBoardService#validate(io.mosip.
	 * registration.dto.biometric.BiometricDTO)
	 */
	@Override
	public ResponseDTO validate(BiometricDTO biometricDTO) {

		ResponseDTO responseDTO = null;

		FaceDetailsDTO photoDetails = biometricDTO.getOperatorBiometricDTO().getFaceDetailsDTO();

		long count = biometricDTO.getOperatorBiometricDTO().getFingerprintDetailsDTO().stream()
				.flatMap(o -> o.getSegmentedFingerprints().stream()).count();

		count = count + biometricDTO.getOperatorBiometricDTO().getIrisDetailsDTO().size();

		count = count + (photoDetails == null ? 0 : 1);

		// API for validating biometrics need to be implemented

		if (count >= UserOnBoardThresholdLimit) {

			responseDTO = save(biometricDTO);

		} else {

			responseDTO = buildErrorRespone(RegistrationConstants.USER_ON_BOARDING_THRESHOLD_NOT_MET_CODE,
					RegistrationConstants.USER_ON_BOARDING_THRESHOLD_NOT_MET_MSG);
		}

		return responseDTO;
	}

	/**
	 * Save.
	 *
	 * @param biometricDTO the biometric DTO
	 * @return the string
	 */
	private ResponseDTO save(BiometricDTO biometricDTO) {
		
		ResponseDTO responseDTO = null;
		String onBoardingResponse = RegistrationConstants.EMPTY;
		
		try {
			
			onBoardingResponse = userOnBoardDao.insert(biometricDTO);

			if (onBoardingResponse.equals(RegistrationConstants.USER_ON_BOARDING_SUCCESS_RESPONSE)) {

				SuccessResponseDTO sucessResponse = new SuccessResponseDTO();
				sucessResponse.setCode(RegistrationConstants.USER_ON_BOARDING_SUCCESS_CODE);
				sucessResponse.setInfoType(RegistrationConstants.ALERT_INFORMATION);
				sucessResponse.setMessage(RegistrationConstants.USER_ON_BOARDING_SUCCESS_MSG);
				responseDTO = new ResponseDTO();
				responseDTO.setSuccessResponseDTO(sucessResponse);

			}

		} catch (RegBaseUncheckedException uncheckedException) {

			LOGGER.error(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
					uncheckedException.getMessage() + onBoardingResponse);

			responseDTO = buildErrorRespone(RegistrationConstants.USER_ON_BOARDING_EXCEPTION_MSG_CODE,
					RegistrationConstants.USER_ON_BOARDING_ERROR_RESPONSE);
			
		} catch (RuntimeException runtimeException) {

			LOGGER.error(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + onBoardingResponse);

			responseDTO = buildErrorRespone(RegistrationConstants.USER_ON_BOARDING_EXCEPTION_MSG_CODE,
					RegistrationConstants.USER_ON_BOARDING_ERROR_RESPONSE);
		}

		return responseDTO;

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

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.UserOnboardService#getStationID(java.lang.String)
	 */
	@Override
	public Map<String,String> getMachineCenterId() {

		Map<String,String> centerIdMap = new HashMap<>();
		String stationID = "";
		String centerID = "";

		try {
			
			String macId = RegistrationSystemPropertiesChecker.getMachineId();
			// get stationID
			stationID = userOnBoardDao.getStationID(macId);
			// get CenterID
			centerID = userOnBoardDao.getCenterID(stationID);
			
			centerIdMap.put(RegistrationConstants.USER_STATION_ID, stationID);
			centerIdMap.put(RegistrationConstants.USER_CENTER_ID, centerID);
			
			
		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, regBaseCheckedException.getMessage());
		}

		return centerIdMap;
	}

}
