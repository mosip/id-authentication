package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_USER_ONBOARD;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.UserOnBoardDao;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.UserOnBoardService;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
@Service
public class UserOnBoardServiceImpl implements UserOnBoardService {
	
	@Autowired
	private UserOnBoardDao userOnBoardDao;
	
	@Value("${USER_ON_BOARD_THRESHOLD_LIMIT}")
	private int UserOnBoardThresholdLimit;
	
	/**
	 * logger for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(UserOnBoardServiceImpl.class);
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.UserOnBoardService#validate(io.mosip.
	 * registration.dto.biometric.BiometricDTO)
	 */
	@Override
	public ResponseDTO validate(BiometricDTO biometricDTO) {

		ResponseDTO responseDTO = null;

		if (null != biometricDTO) {

			int count = 0;

			List<FingerprintDetailsDTO> fingerPrints = biometricDTO.getOperatorBiometricDTO()
					.getFingerprintDetailsDTO();

			List<IrisDetailsDTO> iries = biometricDTO.getOperatorBiometricDTO().getIrisDetailsDTO();

			FaceDetailsDTO photoDetails = biometricDTO.getOperatorBiometricDTO().getFaceDetailsDTO();

			if (null != photoDetails) {

				count = fingerPrints.size() + iries.size() + 1;

			} else {

				count = fingerPrints.size() + iries.size();
			}

			// API for validating biometrics need to be implemented

			if (count >= UserOnBoardThresholdLimit) {

				responseDTO = save(biometricDTO);

			} else {

				responseDTO = buildErrorRespone(RegistrationConstants.USER_ON_BOARDING_THRESHOLD_NOT_MET_CODE,
						RegistrationConstants.USER_ON_BOARDING_THRESHOLD_NOT_MET_MSG);
			}

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

}
