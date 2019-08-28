package io.mosip.registration.service.sync.impl;

import static io.mosip.registration.constants.LoggerConstants.REGISTRATION_PUBLIC_KEY_SYNC;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.security.KeyManagementException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.PolicySyncDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.KeyStore;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.sync.PolicySyncService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

/**
 * 
 * It provides the method to download the Mosip public key specific to the user's local machines and center specific and store 
 * the same into local db for further usage during registration process. The key has expiry period. Based on the expiry period the 
 * new key would be downloaded from the server through this service by triggering from batch process.  
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Service
public class PolicySyncServiceImpl extends BaseService implements PolicySyncService {
	@Autowired
	private PolicySyncDAO policySyncDAO;

	private static final Logger LOGGER = AppConfig.getLogger(PolicySyncServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.PolicySyncService#fetchPolicy(centerId)
	 */
	@Override
	synchronized public ResponseDTO fetchPolicy() throws RegBaseCheckedException {
		LOGGER.debug("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID,
				"sync the public key is started");
		KeyStore keyStore = null;
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			//if(null!getCenterId(getStationId(getMacAddress()));
			String centerMachineId = getCenterId(getStationId(getMacAddress())) + "_" + getStationId(getMacAddress());

			if (!RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
				LOGGER.error("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID, "user is not in online");
				setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_CLIENT_NOT_ONLINE_ERROR_MESSAGE, null);
			} else {
				
				keyStore = policySyncDAO.getPublicKey(centerMachineId);

				if (keyStore != null) {

					Date validDate = new Date(keyStore.getValidTillDtimes().getTime());

					if (validDate
							.compareTo(new Date(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()).getTime())) <= 0
							|| validDate.compareTo(
									new Date(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()).getTime())) <= Integer.valueOf((String.valueOf(ApplicationContext.map().get(RegistrationConstants.KEY_NAME))))) {

						if (validDate.compareTo(
								new Date(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()).getTime())) <= Integer.valueOf((String.valueOf(ApplicationContext.map().get(RegistrationConstants.KEY_NAME))))
								&& isVaildKey(centerMachineId)) {
							
							
							getPublicKey(responseDTO, centerMachineId, LocalDateTime.from(validDate.toInstant().atZone(ZoneId.of("UTC"))).plusDays(1).toString()+"Z");
						} else if (validDate.compareTo(
								new Date(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()).getTime())) <= 0) {
							getPublicKey(responseDTO, centerMachineId, DateUtils.getUTCCurrentDateTimeString());
						}

					}

					responseDTO = setSuccessResponse(responseDTO, RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE,
							null);
				} else {

					getPublicKey(responseDTO, centerMachineId, DateUtils.getUTCCurrentDateTimeString());

				}
			}
		} catch (KeyManagementException | IOException | java.security.NoSuchAlgorithmException exception) {
			LOGGER.error("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
			setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE, null);

		}

		return responseDTO;
	}

	/**
	 * This method invokes the external service 'policysync' to download the public key with respect to local center and machine id combination. 
	 * And store the key into the local database for further usage during registration process. 
	 *
	 * @param responseDTO the response DTO
	 * @param centerMachineId the center machine id
	 * @throws KeyManagementException the key management exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws java.security.NoSuchAlgorithmException the no such algorithm exception
	 * @throws RegBaseCheckedException 
	 */
	@SuppressWarnings("unchecked")
	public synchronized void getPublicKey(ResponseDTO responseDTO, String centerMachineId,String validDate)
			throws KeyManagementException, IOException, java.security.NoSuchAlgorithmException, RegBaseCheckedException {
		LOGGER.debug("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID,
				getCenterId(getStationId(getMacAddress())));
		if(validate(responseDTO,centerMachineId,getCenterId(getStationId(getMacAddress()))))
{
		KeyStore keyStore = new KeyStore();
		List<ErrorResponseDTO> erResponseDTOs = new ArrayList<>();
		Map<String, String> requestParams = new HashMap<>();
		requestParams.put(RegistrationConstants.TIME_STAMP, validDate);
		requestParams.put(RegistrationConstants.REF_ID, centerMachineId);
		try {
			LinkedHashMap<String, Object> publicKeySyncResponse = (LinkedHashMap<String, Object>) serviceDelegateUtil
					.get(RegistrationConstants.SERVICE_NAME, requestParams, false,
							RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
			if (null != publicKeySyncResponse.get(RegistrationConstants.RESPONSE)) {

				LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) publicKeySyncResponse
						.get(RegistrationConstants.RESPONSE);
				keyStore.setId(UUID.randomUUID().toString());
				keyStore.setPublicKey(responseMap.get(RegistrationConstants.PUBLIC_KEY).toString().getBytes());
				LocalDateTime issuedAt = DateUtils
						.parseToLocalDateTime(responseMap.get(RegistrationConstants.ISSUED_AT).toString());
				LocalDateTime expiryAt = DateUtils
						.parseToLocalDateTime(responseMap.get(RegistrationConstants.EXPIRY_AT).toString());
				keyStore.setValidFromDtimes(Timestamp.valueOf(issuedAt));
				keyStore.setValidTillDtimes(Timestamp.valueOf(expiryAt));
				keyStore.setCreatedBy(getUserIdFromSession());
				keyStore.setCreatedDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
				keyStore.setRefId(centerMachineId);
				policySyncDAO.updatePolicy(keyStore);
				responseDTO = setSuccessResponse(responseDTO, RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE, null);
				LOGGER.info("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID,
						"sync the public key is completed");

			} else {
				ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
				errorResponseDTO.setCode(RegistrationConstants.ERRORS);

				errorResponseDTO.setMessage(publicKeySyncResponse.size() > 0
						? ((List<LinkedHashMap<String, String>>) publicKeySyncResponse
								.get(RegistrationConstants.ERRORS)).get(0).get(RegistrationConstants.ERROR_MSG)
						: "Public key Sync rest call Failure");
				erResponseDTOs.add(errorResponseDTO);
				responseDTO.setErrorResponseDTOs(erResponseDTOs);
				LOGGER.info(REGISTRATION_PUBLIC_KEY_SYNC, APPLICATION_NAME, APPLICATION_ID,
						((publicKeySyncResponse.size() > 0)
								? ((List<LinkedHashMap<String, String>>) publicKeySyncResponse
										.get(RegistrationConstants.ERRORS)).get(0).get(RegistrationConstants.ERROR_MSG)
								: "Public key Sync Restful service error"));
			}

		} catch (HttpClientErrorException | RegBaseCheckedException exception) {
			LOGGER.error("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
			setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE, null);

		}

	}}

	/**
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.sync.PolicySyncService#checkKeyValidation()
	 */
	@Override
	public ResponseDTO checkKeyValidation() {

		LOGGER.info("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID, "Key Validation is started");

		ResponseDTO responseDTO = new ResponseDTO();

		try {

			KeyStore keyStore = policySyncDAO
					.getPublicKey(getCenterId(getStationId(getMacAddress())) + "_" + getStationId(getMacAddress()));

			if (keyStore != null) {
				String val = getGlobalConfigValueOf(RegistrationConstants.KEY_NAME);
				if (val != null) {

					/* Get Calendar instance */
					Calendar cal = Calendar.getInstance();
					cal.setTime(new Timestamp(System.currentTimeMillis()));
					cal.add(Calendar.DATE, +Integer.parseInt(val));

					/* Compare Key Validity Date with currentDate+configuredDays */
					if (keyStore.getValidTillDtimes().after(new Timestamp(cal.getTimeInMillis()))) {
						setSuccessResponse(responseDTO, RegistrationConstants.VALID_KEY, null);
					} else {
						setErrorResponse(responseDTO, RegistrationConstants.INVALID_KEY, null);
					}

				}
			} else {
				setErrorResponse(responseDTO, RegistrationConstants.INVALID_KEY, null);
			}
		} catch (RuntimeException runtimeException) {

			LOGGER.info("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
			setErrorResponse(responseDTO, RegistrationConstants.INVALID_KEY, null);
		}

		LOGGER.info("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID, "Key Validation is started");

		return responseDTO;

	}
	
	private boolean validate(ResponseDTO responseDTO,String centerMachineId,String centerId) throws RegBaseCheckedException
	{
		
		if(responseDTO!=null)
		{
			if(centerMachineId!=null&&centerId!=null)
			{
				return true;
			}
			else
			{
				throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_POLICY_SYNC_SERVICE_IMPL_CENTERMACHINEID.getErrorCode(),RegistrationExceptionConstants.REG_POLICY_SYNC_SERVICE_IMPL_CENTERMACHINEID.getErrorMessage());
			}
		}
		else
		{
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_POLICY_SYNC_SERVICE_IMPL.getErrorCode(),RegistrationExceptionConstants.REG_POLICY_SYNC_SERVICE_IMPL.getErrorMessage());
		}
		
	}
	
	/**
	 * Checks if is vaild key.
	 *
	 * @return true, if is vaild key
	 */
	private boolean isVaildKey(String centerMachineId) {
		Date currentDate = new Date(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()).getTime());
		List<KeyStore> keyStoreList = policySyncDAO.getAllKeyStore(centerMachineId);

		if (!keyStoreList.isEmpty()) {
			List<KeyStore> vaildKey = keyStoreList.stream()
					.filter(keyStore -> (keyStore.getValidFromDtimes().compareTo(currentDate) >= 0
							&& keyStore.getValidTillDtimes().compareTo(currentDate) >= 0))
					.collect(Collectors.toList());

			return vaildKey.isEmpty();
		}
		return false;

	}

}
