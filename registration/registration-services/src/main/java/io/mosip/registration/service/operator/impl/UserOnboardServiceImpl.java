package io.mosip.registration.service.operator.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_USER_ONBOARD;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.IntStream;

import javax.crypto.SecretKey;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
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
					LinkedHashMap<String, Object> dataBlockFinger = new LinkedHashMap<>();
					Map<String, Object> data = new HashMap<>();
					data.put(RegistrationConstants.ON_BOARD_TIME_STAMP, DateUtils.getUTCCurrentDateTimeString());
					// data.put(RegistrationConstants.TRANSACTION_ID,
					// RegistrationConstants.TRANSACTION_ID_VALUE);
					// data.put(RegistrationConstants.DEVICE_PROVIDER_ID,
					// RegistrationConstants.ON_BOARD_COGENT);
					data.put(RegistrationConstants.ON_BOARD_BIO_TYPE, RegistrationConstants.ON_BOARD_FINGER_ID);
//					data.put(RegistrationConstants.ON_BOARD_BIO_SUB_TYPE,
//							RegistrationConstants.userOnBoardBioFlag.get(finger.getFingerType()));
					data.put(RegistrationConstants.ON_BOARD_BIO_SUB_TYPE, "UNKNOWN");
					SplittedEncryptedData responseMap = getSessionKey(data, finger.getFingerPrintISOImage());
					data.put(RegistrationConstants.ON_BOARD_BIO_VALUE, responseMap.getEncryptedData());
					try {
						dataBlockFinger.put(RegistrationConstants.ON_BOARD_BIO_DATA,
								CryptoUtil.encodeBase64(new ObjectMapper().writeValueAsString(data).getBytes()));
					} catch (IOException exIoException) {
						LOGGER.error(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
								ExceptionUtils.getStackTrace(exIoException));
					}
					dataBlockFinger.put("hash", "");
					dataBlockFinger.put("sessionKey", responseMap.getEncryptedSessionKey());
					dataBlockFinger.put("signature", "");
					listOfBiometric.add(dataBlockFinger);

				});
				requestMap.put(RegistrationConstants.ON_BOARD_BIOMETRICS, listOfBiometric);
			});

			biometricDTO.getOperatorBiometricDTO().getIrisDetailsDTO().forEach(iris -> {

				LinkedHashMap<String, Object> dataBlockIris = new LinkedHashMap<>();
				Map<String, Object> data = new HashMap<>();
				data.put(RegistrationConstants.ON_BOARD_TIME_STAMP, DateUtils.getUTCCurrentDateTimeString());
				// data.put(RegistrationConstants.TRANSACTION_ID,
				// RegistrationConstants.TRANSACTION_ID_VALUE);
				// data.put(RegistrationConstants.DEVICE_PROVIDER_ID,
				// RegistrationConstants.ON_BOARD_COGENT);
				data.put(RegistrationConstants.ON_BOARD_BIO_TYPE, RegistrationConstants.ON_BOARD_IRIS_ID);
				data.put(RegistrationConstants.ON_BOARD_BIO_SUB_TYPE, "UNKNOWN");
//				data.put(RegistrationConstants.ON_BOARD_BIO_SUB_TYPE,
//						RegistrationConstants.userOnBoardBioFlag.get(iris.getIrisImageName()));
				SplittedEncryptedData responseMap=getSessionKey(data,iris.getIrisIso());
				data.put(RegistrationConstants.ON_BOARD_BIO_VALUE, responseMap.getEncryptedData());
				try {
					dataBlockIris.put(RegistrationConstants.ON_BOARD_BIO_DATA,
							CryptoUtil.encodeBase64(new ObjectMapper().writeValueAsString(data).getBytes()));
				} catch (IOException exIoException) {
					LOGGER.error(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
							ExceptionUtils.getStackTrace(exIoException));
				}
				dataBlockIris.put("hash", "");
				dataBlockIris.put("sessionKey",responseMap.getEncryptedSessionKey());
				dataBlockIris.put("signature", "");
				listOfBiometric.add(dataBlockIris);

			});

			requestMap.put(RegistrationConstants.ON_BOARD_BIOMETRICS, listOfBiometric);

			LinkedHashMap<String, Object> biometricMap = new LinkedHashMap<>();
			Map<String, Object> data = new HashMap<>();
			data.put(RegistrationConstants.ON_BOARD_TIME_STAMP, DateUtils.getUTCCurrentDateTimeString());
			// requestDataMap.put(RegistrationConstants.TRANSACTION_ID,
			// RegistrationConstants.TRANSACTION_ID_VALUE);
			// requestDataMap.put(RegistrationConstants.DEVICE_PROVIDER_ID,
			// RegistrationConstants.ON_BOARD_COGENT);
			data.put(RegistrationConstants.ON_BOARD_BIO_TYPE, RegistrationConstants.ON_BOARD_FACE_ID);
			data.put(RegistrationConstants.ON_BOARD_BIO_SUB_TYPE, RegistrationConstants.ON_BOARD_FACE);
			SplittedEncryptedData responseMap=getSessionKey(data,biometricDTO.getOperatorBiometricDTO().getFace().getFaceISO());
			data.put(RegistrationConstants.ON_BOARD_BIO_VALUE,
					responseMap.getEncryptedData());

			try {
				
				biometricMap.put(RegistrationConstants.ON_BOARD_BIO_DATA,
						CryptoUtil.encodeBase64(new ObjectMapper().writeValueAsString(data).getBytes()));
			} catch (IOException exIoException) {
				LOGGER.error(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
						ExceptionUtils.getStackTrace(exIoException));
				setErrorResponse(responseDTO, RegistrationConstants.USER_ON_BOARDING_EXCEPTION, null);
			}
			
			biometricMap.put("hash", "");
			biometricMap.put("sessionKey", responseMap.getEncryptedSessionKey());
			biometricMap.put("signature", "");
			
			listOfBiometric.add(biometricMap);

			try {
				LOGGER.info(LOG_REG_USER_ONBOARD, APPLICATION_NAME, APPLICATION_ID,
						JsonUtils.javaObjectToJsonString(listOfBiometric));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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
	
	private SplittedEncryptedData getSessionKey(Map<String, Object> requestMap, byte[] data) {
		SplittedEncryptedData splittedData=null;
		Map<String, Object> mapRequest = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		String timestamp = (String) requestMap.get(RegistrationConstants.ON_BOARD_TIME_STAMP);
		String aad = CryptoUtil.encodeBase64String(timestamp.substring(timestamp.length() - 12).getBytes());
		String salt = CryptoUtil.encodeBase64String(timestamp.substring(timestamp.length() - 16).getBytes());
		map.put("aad", aad);
		map.put("applicationId", "IDA");
		map.put("data", CryptoUtil.encodeBase64(data));
		map.put("referenceId", "INTRENAL");
		map.put("salt", salt);
		map.put("timeStamp", DateUtils.getUTCCurrentDateTimeString());
		mapRequest.put("request",map);
		mapRequest.put("requesttime", DateUtils.getUTCCurrentDateTimeString());
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> responseResult = (Map<String, Object>) serviceDelegateUtil
					.post(RegistrationConstants.SESSION_KEY_URL, mapRequest, RegistrationConstants.JOB_TRIGGER_POINT_USER);
			LinkedHashMap<String,Object> splitData=(LinkedHashMap<String, Object>) responseResult.get("response");
			 splittedData=splitEncryptedData((String) splitData.get("data"));
			
		} catch (HttpClientErrorException | ResourceAccessException | SocketTimeoutException
				| RegBaseCheckedException e) {
			e.printStackTrace();
		}

		return splittedData;

	}
	

	public SplittedEncryptedData splitEncryptedData(String data) {
		byte[] dataBytes = CryptoUtil.decodeBase64(data);
		byte[][] splits = splitAtFirstOccurance(dataBytes, String.valueOf(ApplicationContext.map().get("mosip.kernel.data-key-splitter")).getBytes());
		return new SplittedEncryptedData(CryptoUtil.encodeBase64(splits[0]), CryptoUtil.encodeBase64(splits[1]));
	} 
	
	

	
	private static byte[][] splitAtFirstOccurance(byte[] strBytes, byte[] sepBytes) {
		int index = findIndex(strBytes, sepBytes);
		if (index >= 0) {
			byte[] bytes1 = new byte[index];
			byte[] bytes2 = new byte[strBytes.length - (bytes1.length + sepBytes.length)];
			System.arraycopy(strBytes, 0, bytes1, 0, bytes1.length);
			System.arraycopy(strBytes, (bytes1.length + sepBytes.length), bytes2, 0, bytes2.length);
			return new byte[][] { bytes1, bytes2 };
		} else {
			return new byte[][] { strBytes, new byte[0] };
		}
	}
	
	private static int findIndex(byte arr[], byte[] subarr) {
		int len = arr.length;
		int subArrayLen = subarr.length;
		return IntStream.range(0, len).filter(currentIndex -> {
			if ((currentIndex + subArrayLen) <= len) {
				byte[] sArray = new byte[subArrayLen];
				System.arraycopy(arr, currentIndex, sArray, 0, subArrayLen);
				return Arrays.equals(sArray, subarr);
			}
			return false;
		}).findFirst() // first occurence
				.orElse(-1); // No element found
	}




	public static class SplittedEncryptedData {
		private String encryptedSessionKey;
		private String encryptedData;
		
		public SplittedEncryptedData() {
			super();
		}
		
		public SplittedEncryptedData(String encryptedSessionKey,String encryptedData) {
			super();
			this.encryptedData = encryptedData;
			this.encryptedSessionKey = encryptedSessionKey;
		}
		
		
		public String getEncryptedData() {
			return encryptedData;
		}
		public void setEncryptedData(String encryptedData) {
			this.encryptedData = encryptedData;
		}
		public String getEncryptedSessionKey() {
			return encryptedSessionKey;
		}
		public void setEncryptedSessionKey(String encryptedSessionKey) {
			this.encryptedSessionKey = encryptedSessionKey;
		}
	}
}
