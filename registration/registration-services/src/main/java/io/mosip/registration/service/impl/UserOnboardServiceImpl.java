package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_USER_ONBOARD;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
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

		int UserOnBoardThresholdLimit =  Integer.parseInt((String) ApplicationContext.map().get("USER_ON_BOARD_THRESHOLD_LIMIT"));

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

			responseDTO = errorRespone(RegistrationConstants.USER_ON_BOARDING_THRESHOLD_NOT_MET_CODE,
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

		LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "Entering save method");

		try {

			LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "Entering insert method");

			onBoardingResponse = userOnBoardDao.insert(biometricDTO);

			if (onBoardingResponse.equals(RegistrationConstants.success)) {

				SuccessResponseDTO sucessResponse = new SuccessResponseDTO();
				sucessResponse.setCode(RegistrationConstants.USER_ON_BOARDING_SUCCESS_CODE);
				sucessResponse.setInfoType(RegistrationConstants.ALERT_INFORMATION);
				sucessResponse.setMessage(RegistrationConstants.USER_ON_BOARDING_SUCCESS_MSG);
				responseDTO = new ResponseDTO();
				responseDTO.setSuccessResponseDTO(sucessResponse);

			}

			LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "user onbaording sucessful");

		} catch (RegBaseUncheckedException uncheckedException) {

			LOGGER.error(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
					uncheckedException.getMessage() + onBoardingResponse);

			responseDTO = errorRespone(RegistrationConstants.USER_ON_BOARDING_EXCEPTION_MSG_CODE,
					RegistrationConstants.USER_ON_BOARDING_ERROR_RESPONSE);

		} catch (RuntimeException runtimeException) {

			LOGGER.error(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + onBoardingResponse);

			responseDTO = errorRespone(RegistrationConstants.USER_ON_BOARDING_EXCEPTION_MSG_CODE,
					RegistrationConstants.USER_ON_BOARDING_ERROR_RESPONSE);
		}

		return responseDTO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.UserOnboardService#getStationID(java.lang.
	 * String)
	 */
	@Override
	public Map<String, String> getMachineCenterId() {

		Map<String, String> mapOfCenterId = new HashMap<>();

		String stationId = RegistrationConstants.EMPTY;
		String centerId = RegistrationConstants.EMPTY;

		LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "fetching mac Id....");

		try {

			// to get mac Id
			String systemMacId = RegistrationSystemPropertiesChecker.getMachineId();

			// get stationID
			stationId = userOnBoardDao.getStationID(systemMacId);

			// get CenterID
			centerId = userOnBoardDao.getCenterID(stationId);

			// setting data into map
			mapOfCenterId.put(RegistrationConstants.USER_STATION_ID, stationId);
			mapOfCenterId.put(RegistrationConstants.USER_CENTER_ID, centerId);

			LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
					"station Id = " + stationId + "---->" + "center Id = " + centerId);

		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, regBaseCheckedException.getMessage());
		}

		return mapOfCenterId;
	}

	/**
	 * Builds the error respone.
	 *
	 * @param errCode the error code
	 * @param errMsg  the message
	 * @return the response DTO
	 */
	private ResponseDTO errorRespone(final String errCode, final String errMsg) {

		ResponseDTO responseDto = new ResponseDTO();

		LinkedList<ErrorResponseDTO> errResponsesList = new LinkedList<>();

		/* Error response Dto */
		ErrorResponseDTO errResponse = new ErrorResponseDTO();
		errResponse.setCode(errCode);
		errResponse.setInfoType(RegistrationConstants.ERROR);
		errResponse.setMessage(errMsg);
		errResponsesList.add(errResponse);

		responseDto.setErrorResponseDTOs(errResponsesList);

		return responseDto;
	}

}
