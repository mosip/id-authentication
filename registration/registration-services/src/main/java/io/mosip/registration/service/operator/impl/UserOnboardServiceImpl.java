package io.mosip.registration.service.operator.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_USER_ONBOARD;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.crypto.SecretKey;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.UserOnboardDAO;
import io.mosip.registration.dto.PublicKeyResponse;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;
import io.mosip.registration.util.publickey.PublicKeyGenerationUtil;

/**
 * Implementation for {@link UserOnboardService}
 * 
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
@Service
public class UserOnboardServiceImpl extends BaseService implements UserOnboardService {

	@Autowired
	private UserOnboardDAO userOnBoardDao;

	@Autowired
	private KeyGenerator keyGenerator;

	@Autowired
    private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> cryptoCore;

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
	public ResponseDTO validate(BiometricDTO biometricDTO) throws RegBaseCheckedException {

		ResponseDTO responseDTO = new ResponseDTO();
		if (dtoNullCheck(biometricDTO)) {
			Map<String, Object> idaRequestMap = new LinkedHashMap<>();

			idaRequestMap.put(RegistrationConstants.ID, RegistrationConstants.IDENTITY);
			idaRequestMap.put(RegistrationConstants.VERSION, RegistrationConstants.PACKET_SYNC_VERSION);
			idaRequestMap.put(RegistrationConstants.REQUEST_TIME, DateUtils.getUTCCurrentDateTimeString());
			idaRequestMap.put(RegistrationConstants.TRANSACTION_ID, RegistrationConstants.TRANSACTION_ID_VALUE);
			Map<String, Boolean> tempMap = new HashMap<>();
			tempMap.put(RegistrationConstants.BIO, true);
			idaRequestMap.put(RegistrationConstants.REQUEST_AUTH, tempMap);
			idaRequestMap.put(RegistrationConstants.CONSENT_OBTAINED, true);
			idaRequestMap.put(RegistrationConstants.INDIVIDUAL_ID, SessionContext.userContext().getUserId());
			idaRequestMap.put(RegistrationConstants.INDIVIDUAL_ID_TYPE, RegistrationConstants.USER_ID_CODE);
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
							RegistrationConstants.userOnBoardBioFlag.get(finger.getFingerType()));
					data.put(RegistrationConstants.ON_BOARD_BIO_VALUE,
							CryptoUtil.encodeBase64(finger.getFingerPrintISOImage()));
					try {
						data1.put(RegistrationConstants.ON_BOARD_BIO_DATA,
								CryptoUtil.encodeBase64(new ObjectMapper().writeValueAsString(data).getBytes()));
					} catch (IOException exIoException) {
						LOGGER.error(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
								ExceptionUtils.getStackTrace(exIoException));
					}
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
						RegistrationConstants.userOnBoardBioFlag.get(iris.getIrisImageName()));
				data.put(RegistrationConstants.ON_BOARD_BIO_VALUE, CryptoUtil.encodeBase64(iris.getIrisIso()));
				try {
					data1.put(RegistrationConstants.ON_BOARD_BIO_DATA,
							CryptoUtil.encodeBase64(new ObjectMapper().writeValueAsString(data).getBytes()));
				} catch (IOException exIoException) {
					LOGGER.error(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
							ExceptionUtils.getStackTrace(exIoException));
				}
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

			try {
				requestDataMap.put(RegistrationConstants.ON_BOARD_BIO_VALUE,
						CryptoUtil.encodeBase64(IOUtils.resourceToByteArray((RegistrationConstants.FACE_ISO))));

				biometricMap.put(RegistrationConstants.ON_BOARD_BIO_DATA,
						CryptoUtil.encodeBase64(new ObjectMapper().writeValueAsString(requestDataMap).getBytes()));
			} catch (IOException exIoException) {
				LOGGER.error(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
						ExceptionUtils.getStackTrace(exIoException));
				setErrorResponse(responseDTO, RegistrationConstants.USER_ON_BOARDING_EXCEPTION, null);
			}
			listOfBiometric.add(biometricMap);

			requestMap.put(RegistrationConstants.TRANSACTION_ID, RegistrationConstants.TRANSACTION_ID_VALUE);
			requestMap.put(RegistrationConstants.ON_BOARD_BIOMETRICS, listOfBiometric);
			requestMap.put(RegistrationConstants.ON_BOARD_TIME_STAMP, DateUtils.getUTCCurrentDateTimeString());

			Map<String, String> requestParamMap = new LinkedHashMap<>();
			requestParamMap.put(RegistrationConstants.REF_ID, RegistrationConstants.IDA_REFERENCE_ID);
			requestParamMap.put(RegistrationConstants.TIME_STAMP, DateUtils.getUTCCurrentDateTimeString());
			if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
				responseDTO = isIdaAuthRequired(idaRequestMap, requestMap, biometricDTO, requestParamMap);
			} else {
				LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, RegistrationConstants.NO_INTERNET);
				setErrorResponse(responseDTO, RegistrationConstants.NO_INTERNET, null);
			}

		} else {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_BIOMETRIC_DTO_NULL.getErrorCode(),
					RegistrationExceptionConstants.REG_BIOMETRIC_DTO_NULL.getErrorMessage());
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

		ResponseDTO responseDTO = new ResponseDTO();
		String onBoardingResponse = RegistrationConstants.EMPTY;

		LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "Entering save method");

		try {

			onBoardingResponse = userOnBoardDao.insert(biometricDTO);

			if (onBoardingResponse.equalsIgnoreCase(RegistrationConstants.SUCCESS)) {

				LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "operator details inserted");

				if ((RegistrationConstants.SUCCESS).equalsIgnoreCase(userOnBoardDao.save())) {

					LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
							"center user machine details inserted");

					setSuccessResponse(responseDTO, RegistrationConstants.USER_ON_BOARDING_SUCCESS_MSG, null);

					LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "user onboarding sucessful");
				}
			}

		} catch (RegBaseUncheckedException uncheckedException) {

			LOGGER.error(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, uncheckedException.getMessage()
					+ onBoardingResponse + ExceptionUtils.getStackTrace(uncheckedException));

			setErrorResponse(responseDTO, RegistrationConstants.USER_ON_BOARDING_ERROR_RESPONSE, null);

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
	 * User on board status flag.
	 *
	 * @param onBoardResponseMap the on board response map
	 * @return the boolean
	 */
	@SuppressWarnings("unchecked")
	private Boolean userOnBoardStatusFlag(LinkedHashMap<String, Object> onBoardResponseMap) {

		Boolean userOnbaordFlag = false;

		if (null != onBoardResponseMap
				&& null != onBoardResponseMap.get(RegistrationConstants.RESPONSE)
				&& null == onBoardResponseMap.get(RegistrationConstants.ERRORS)) {
			LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) onBoardResponseMap
					.get(RegistrationConstants.RESPONSE);
			LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "authStatus true");
			userOnbaordFlag = (Boolean) responseMap.get(RegistrationConstants.ON_BOARD_AUTH_STATUS);
		} else if (null != onBoardResponseMap && null != onBoardResponseMap.get(RegistrationConstants.ERRORS)) {
			List<LinkedHashMap<String, Object>> listOfFailureResponse = (List<LinkedHashMap<String, Object>>) onBoardResponseMap
					.get(RegistrationConstants.ERRORS);
			LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) onBoardResponseMap
					.get(RegistrationConstants.RESPONSE);
			userOnbaordFlag = (Boolean) responseMap.get(RegistrationConstants.ON_BOARD_AUTH_STATUS);
			LOGGER.debug(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, listOfFailureResponse.toString());
		}

		return userOnbaordFlag;

	}

	@Override
	public Timestamp getLastUpdatedTime(String usrId) {

		return userOnBoardDao.getLastUpdatedTime(usrId);
	}

	/**
	 * Dto null check.
	 *
	 * @param biometricDTO the biometric DTO
	 * @return true, if successful
	 */
	private boolean dtoNullCheck(BiometricDTO biometricDTO) {

		if (null != biometricDTO && null != biometricDTO.getOperatorBiometricDTO()) {
			LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
					"biometricDTO/operator bio metrics are mandatroy");
			return true;
		} else {
			return false;
		}

	}

	@SuppressWarnings("unchecked")
	private ResponseDTO isIdaAuthRequired(Map<String, Object> idaRequestMap, Map<String, Object> requestMap,
			BiometricDTO biometricDTO, Map<String, String> requestParamMap) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			if (RegistrationConstants.ENABLE.equalsIgnoreCase(
					(String) ApplicationContext.map().get(RegistrationConstants.USER_ON_BOARD_IDA_AUTH))) {
				PublicKeyResponse<String> publicKeyResponse = null;

				publicKeyResponse = (PublicKeyResponse<String>) serviceDelegateUtil.get(
						RegistrationConstants.PUBLIC_KEY_IDA_REST, requestParamMap, false,
						RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);

				LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "Getting Public Key.....");

				if (null != publicKeyResponse && !publicKeyResponse.getResponse().isEmpty()
						&& publicKeyResponse.getResponse().size() > 0) {

					// Getting Public Key
					PublicKey publicKey = PublicKeyGenerationUtil.generatePublicKey(publicKeyResponse.getResponse()
							.get(RegistrationConstants.PUBLIC_KEY).toString().getBytes());

					LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "Getting Symmetric Key.....");
					// Symmetric key alias session key
					SecretKey symmentricKey = keyGenerator.getSymmetricKey();

					LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "preparing request.....");
					// request
					idaRequestMap.put(RegistrationConstants.ON_BOARD_REQUEST,
							CryptoUtil.encodeBase64(cryptoCore.symmetricEncrypt(symmentricKey,
									new ObjectMapper().writeValueAsString(requestMap).getBytes(),null)));

					LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "preparing request HMAC.....");
					// requestHMAC
					idaRequestMap
							.put(RegistrationConstants.ON_BOARD_REQUEST_HMAC,
									CryptoUtil.encodeBase64(cryptoCore.symmetricEncrypt(symmentricKey,
											HMACUtils.digestAsPlainText(HMACUtils.generateHash(
													new ObjectMapper().writeValueAsString(requestMap).getBytes()))
													.getBytes(),null)));

					LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
							"preparing request Session Key.....");
					// requestSession Key
					idaRequestMap.put(RegistrationConstants.ON_BOARD_REQUEST_SESSION_KEY, CryptoUtil
							.encodeBase64(cryptoCore.asymmetricEncrypt(publicKey, symmentricKey.getEncoded())));

					LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID, "Ida Auth rest calling.....");

					LinkedHashMap<String, Object> onBoardResponse = (LinkedHashMap<String, Object>) serviceDelegateUtil
							.post(RegistrationConstants.ON_BOARD_IDA_VALIDATION, idaRequestMap,
									RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);

					boolean onboardAuthFlag = userOnBoardStatusFlag(onBoardResponse);

					LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
							"User Onboarded authentication flag... :" + onboardAuthFlag);

					if (onboardAuthFlag) {
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

			} else {
				responseDTO = save(biometricDTO);
				LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
						RegistrationConstants.USER_ON_BOARDING_SUCCESS_MSG);
			}

		} catch (RegBaseCheckedException | InvalidKeySpecException | NoSuchAlgorithmException | IOException
				| RuntimeException regBasedCheckedException) {
			LOGGER.error(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(regBasedCheckedException));
			setErrorResponse(responseDTO, RegistrationConstants.USER_ON_BOARDING_EXCEPTION, null);
		}
		return responseDTO;

	}

}
