package io.mosip.registration.service.operator.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_USER_ONBOARD;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
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
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

/**
 * Implementation for {@link UserOnboardService}
 * 
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

<<<<<<< HEAD
		int UserOnBoardThresholdLimit = Integer
				.parseInt((String) ApplicationContext.map().get(RegistrationConstants.USER_ON_BOARD_THRESHOLD_LIMIT));

		ResponseDTO responseDTO = null;
=======
		ResponseDTO responseDTO = new ResponseDTO();

		Map<String, Object> idaRequestMap = new LinkedHashMap<>();

		idaRequestMap.put(RegistrationConstants.ID, RegistrationConstants.IDENTITY);
		idaRequestMap.put(RegistrationConstants.VERSION, RegistrationConstants.PACKET_SYNC_VERSION);
		idaRequestMap.put(RegistrationConstants.REQUEST_TIME, DateUtils.getUTCCurrentDateTimeString());
		idaRequestMap.put(RegistrationConstants.TRANSACTION_ID, RegistrationConstants.TRANSACTION_ID_VALUE);
		Map<String, Boolean> tempMap = new HashMap<>();
		tempMap.put(RegistrationConstants.BIO, true);
		idaRequestMap.put(RegistrationConstants.REQUEST_AUTH, tempMap);
		idaRequestMap.put(RegistrationConstants.CONSENT_OBTAINED, true);
		idaRequestMap.put(RegistrationConstants.INDIVIDUAL_ID,
				SessionContext.userContext().getUserId());
		idaRequestMap.put(RegistrationConstants.INDIVIDUAL_ID_TYPE, "USERID");
		idaRequestMap.put(RegistrationConstants.KEY_INDEX, "");

		List<Map<String, Object>> listOfBiometric = new ArrayList<>();
		Map<String, Object> requestMap = new LinkedHashMap<>();

		biometricDTO.getOperatorBiometricDTO().getFingerprintDetailsDTO().forEach(bio -> {

			bio.getSegmentedFingerprints().forEach(finger -> {
				LinkedHashMap<String, Object> data1 = new LinkedHashMap<>();
				Map<String, Object> data = new HashMap<>();
				data.put(RegistrationConstants.ON_BOARD_TIME_STAMP, DateUtils.getUTCCurrentDateTimeString());
				data.put(RegistrationConstants.TRANSACTION_ID, RegistrationConstants.TRANSACTION_ID_VALUE);
				data.put(RegistrationConstants.DEVICE_PROVIDER_ID, RegistrationConstants.ON_BOARD_COGENT);
				data.put(RegistrationConstants.ON_BOARD_BIO_TYPE, RegistrationConstants.ON_BOARD_FINGER_ID);
				data.put(RegistrationConstants.ON_BOARD_BIO_SUB_TYPE,
						RegistrationConstants.userOnBoardMap.get(finger.getFingerType()));
				data.put(RegistrationConstants.ON_BOARD_BIO_VALUE,
						Base64.getEncoder().encodeToString(finger.getFingerPrint()));
				data1.put(RegistrationConstants.ON_BOARD_BIO_DATA, data);
				listOfBiometric.add(data1);

			});
			requestMap.put(RegistrationConstants.ON_BOARD_BIOMETRICS, listOfBiometric);
		});

		biometricDTO.getOperatorBiometricDTO().getIrisDetailsDTO().forEach(iris -> {

			LinkedHashMap<String, Object> data1 = new LinkedHashMap<>();
			Map<String, Object> data = new HashMap<>();
			data.put(RegistrationConstants.ON_BOARD_TIME_STAMP, DateUtils.getUTCCurrentDateTimeString());
			data.put(RegistrationConstants.TRANSACTION_ID, RegistrationConstants.TRANSACTION_ID_VALUE);
			data.put(RegistrationConstants.DEVICE_PROVIDER_ID, RegistrationConstants.ON_BOARD_COGENT);
			data.put(RegistrationConstants.ON_BOARD_BIO_TYPE, RegistrationConstants.ON_BOARD_IRIS_ID);
			data.put(RegistrationConstants.ON_BOARD_BIO_SUB_TYPE,
					RegistrationConstants.userOnBoardMap.get(iris.getIrisImageName()));
			data.put(RegistrationConstants.ON_BOARD_BIO_VALUE, Base64.getEncoder().encodeToString(iris.getIris()));
			data1.put(RegistrationConstants.ON_BOARD_BIO_DATA, data);
			listOfBiometric.add(data1);

		});
		
		requestMap.put(RegistrationConstants.ON_BOARD_BIOMETRICS, listOfBiometric);

		LinkedHashMap<String, Object> biometricMap = new LinkedHashMap<>();
		Map<String, Object> requestDataMap = new HashMap<>();
		requestDataMap.put(RegistrationConstants.ON_BOARD_TIME_STAMP, DateUtils.getUTCCurrentDateTimeString());
		requestDataMap.put(RegistrationConstants.TRANSACTION_ID, RegistrationConstants.TRANSACTION_ID_VALUE);
		requestDataMap.put(RegistrationConstants.DEVICE_PROVIDER_ID, RegistrationConstants.ON_BOARD_COGENT);
		requestDataMap.put(RegistrationConstants.ON_BOARD_BIO_TYPE, RegistrationConstants.ON_BOARD_FACE_ID);
		requestDataMap.put(RegistrationConstants.ON_BOARD_BIO_SUB_TYPE, RegistrationConstants.ON_BOARD_FACE);

		/*if (RegistrationConstants.ENABLE.equalsIgnoreCase(RegistrationConstants.FACE_DISABLE_FLAG)) {
			requestDataMap.put(RegistrationConstants.ON_BOARD_BIO_VALUE,
					Base64.getEncoder().encodeToString(biometricDTO.getOperatorBiometricDTO().getFace().getFace()));
		}*/
		 
		requestDataMap.put(RegistrationConstants.ON_BOARD_BIO_VALUE, RegistrationConstants.STUB_FACE);
		biometricMap.put(RegistrationConstants.ON_BOARD_BIO_DATA, requestDataMap);
		listOfBiometric.add(biometricMap);

		requestMap.put(RegistrationConstants.TRANSACTION_ID, RegistrationConstants.TRANSACTION_ID_VALUE);
		requestMap.put(RegistrationConstants.ON_BOARD_BIOMETRICS, listOfBiometric);
		requestMap.put(RegistrationConstants.ON_BOARD_TIME_STAMP, DateUtils.getUTCCurrentDateTimeString());

		PublicKeyResponse<String> publicKeyResponse = null;
		Map<String, String> requestParamMap = new LinkedHashMap<>();
		requestParamMap.put(RegistrationConstants.REF_ID, RegistrationConstants.IDA_REFERENCE_ID);
		requestParamMap.put(RegistrationConstants.TIME_STAMP, DateUtils.getUTCCurrentDateTimeString());

		try {

			if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
				publicKeyResponse = (PublicKeyResponse<String>) serviceDelegateUtil.get(
						RegistrationConstants.PUBLIC_KEY_IDA_REST, requestParamMap, false,
						RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);

				LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "Getting Public Key.....");

				if (null != publicKeyResponse && !publicKeyResponse.getResponse().isEmpty()
						&& publicKeyResponse.getResponse().size() > 0) {
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082

		FaceDetailsDTO photoDetails = biometricDTO.getOperatorBiometricDTO().getFace();

<<<<<<< HEAD
		long count = biometricDTO.getOperatorBiometricDTO().getFingerprintDetailsDTO().stream()
				.flatMap(o -> o.getSegmentedFingerprints().stream()).count();

		count = count + biometricDTO.getOperatorBiometricDTO().getIrisDetailsDTO().size();

		count = count + (photoDetails == null ? 0 : 1);

		// API for validating biometrics need to be implemented

		if (RegistrationConstants.ENABLE.equalsIgnoreCase(
				String.valueOf(ApplicationContext.map().get(RegistrationConstants.FINGERPRINT_DISABLE_FLAG)))
				&& RegistrationConstants.ENABLE.equalsIgnoreCase(
						String.valueOf(ApplicationContext.map().get(RegistrationConstants.IRIS_DISABLE_FLAG)))
				&& RegistrationConstants.ENABLE.equalsIgnoreCase(
						String.valueOf(ApplicationContext.map().get(RegistrationConstants.FACE_DISABLE_FLAG)))) {

			if (count >= UserOnBoardThresholdLimit) {
				responseDTO = save(biometricDTO);
=======
					LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "Getting Symmetric Key.....");
					// Symmetric key alias session key
					SecretKey symmentricKey = keyGenerator.getSymmetricKey();

					LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "preparing request.....");
					// request
					idaRequestMap.put(RegistrationConstants.ON_BOARD_REQUEST,
							CryptoUtil.encodeBase64(encryptor.symmetricEncrypt(symmentricKey,
									new ObjectMapper().writeValueAsString(requestMap).getBytes())));

					LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "preparing request HMAC.....");
					// requestHMAC
					idaRequestMap
							.put(RegistrationConstants.ON_BOARD_REQUEST_HMAC,
									CryptoUtil.encodeBase64(encryptor.symmetricEncrypt(symmentricKey,
											HMACUtils.digestAsPlainText(HMACUtils.generateHash(
													new ObjectMapper().writeValueAsString(requestMap).getBytes()))
													.getBytes())));

					LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
							"preparing request Session Key.....");
					// requestSession Key
					idaRequestMap.put(RegistrationConstants.ON_BOARD_REQUEST_SESSION_KEY, CryptoUtil
							.encodeBase64(encryptor.asymmetricPublicEncrypt(publicKey, symmentricKey.getEncoded())));

					LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "Ida Auth rest calling.....");
					LinkedHashMap<String, Object> onBoardResponse = (LinkedHashMap<String, Object>) serviceDelegateUtil
							.post(RegistrationConstants.ON_BOARD_IDA_VALIDATION, idaRequestMap,
									RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);

					boolean onboardAuthFlag = userOnBoardStatusFlag(onBoardResponse);

					LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
							"User Onboarded authentication flag... :" + onboardAuthFlag);

					if (true) {
						responseDTO = save(biometricDTO);
						LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
								RegistrationConstants.USER_ON_BOARDING_SUCCESS_MSG);
					} else {
						LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
								RegistrationConstants.USER_ON_BOARDING_THRESHOLD_NOT_MET_MSG);
						setErrorResponse(responseDTO, RegistrationConstants.USER_ON_BOARDING_THRESHOLD_NOT_MET_MSG,
								onBoardResponse);
					}

				} else {
					LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
							RegistrationConstants.ON_BOARD_PUBLIC_KEY_ERROR);
					setErrorResponse(responseDTO, RegistrationConstants.ON_BOARD_PUBLIC_KEY_ERROR, null);
				}
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
			} else {
				responseDTO = errorRespone(RegistrationConstants.ERROR,
						RegistrationConstants.USER_ON_BOARDING_THRESHOLD_NOT_MET_MSG);
			}

		} else {
			responseDTO = save(biometricDTO);
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
			onBoardingResponse = userOnBoardDao.insert(biometricDTO);

			if (onBoardingResponse.equalsIgnoreCase(RegistrationConstants.SUCCESS)) {

				LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "operator details inserted");
				
				String saveUser = userOnBoardDao.save();
				if (saveUser.equalsIgnoreCase(RegistrationConstants.SUCCESS)) {

					LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
							"center user machine details inserted");

					SuccessResponseDTO sucessResponse = new SuccessResponseDTO();
					sucessResponse.setCode(RegistrationConstants.USER_ON_BOARDING_SUCCESS_CODE);
					sucessResponse.setInfoType(RegistrationConstants.ALERT_INFORMATION);
					sucessResponse.setMessage(RegistrationConstants.USER_ON_BOARDING_SUCCESS_MSG);
					responseDTO = new ResponseDTO();
					responseDTO.setSuccessResponseDTO(sucessResponse);
				}
			}

			LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "user onbaording sucessful");

		} catch (RegBaseUncheckedException uncheckedException) {

			LOGGER.error(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, uncheckedException.getMessage()
					+ onBoardingResponse + ExceptionUtils.getStackTrace(uncheckedException));

			responseDTO = errorRespone(RegistrationConstants.ERROR,
					RegistrationConstants.USER_ON_BOARDING_ERROR_RESPONSE);

		} catch (RuntimeException runtimeException) {

			LOGGER.error(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, runtimeException.getMessage()
					+ onBoardingResponse + ExceptionUtils.getStackTrace(runtimeException));

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

		Map<String, String> mapOfCenterId = new WeakHashMap<>();

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
			LOGGER.error(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
					regBaseCheckedException.getMessage() + ExceptionUtils.getStackTrace(regBaseCheckedException));
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
