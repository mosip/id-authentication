package io.mosip.registration.service.sync.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.security.KeyManagementException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import io.mosip.registration.dto.PublicKeyResponse;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.KeyStore;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.sync.PolicySyncService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

/**
 * it does the key policy synch
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
	synchronized public ResponseDTO fetchPolicy() {
		LOGGER.debug("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID,
				"synch the public key is started");
		KeyStore keyStore = null;
		String centerMachineId=getCenterId(getStationId(getMacAddress()))+"_"+getStationId(getMacAddress());
		ResponseDTO responseDTO = new ResponseDTO();
		if (!RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
			LOGGER.error("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID, "user is not in online");
			setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_CLIENT_NOT_ONLINE_ERROR_MESSAGE, null);
		} else {
			 keyStore = policySyncDAO.getPublicKey(centerMachineId);
 
			if (keyStore != null) {
				Date validDate = new Date(keyStore.getValidTillDtimes().getTime());
				long difference = ChronoUnit.DAYS.between(new Date().toInstant(), validDate.toInstant());
				if (Integer
						.parseInt((String) ApplicationContext.map().get(RegistrationConstants.KEY_NAME)) < difference) {
					setSuccessResponse(responseDTO, RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE, null);
				} else {

					try {
						getPublicKey(responseDTO,centerMachineId);
					} catch (KeyManagementException | IOException | java.security.NoSuchAlgorithmException exception) {
						LOGGER.error("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID,
								exception.getMessage());

						setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE, null);

					}
				}
			} else {
				try {
					getPublicKey(responseDTO,centerMachineId);
				} catch (KeyManagementException | IOException | java.security.NoSuchAlgorithmException exception) {
					LOGGER.error("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID,
							exception.getMessage());
					setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE, null);

				}

			}
		}
		return responseDTO;
	}

	public synchronized void getPublicKey(ResponseDTO responseDTO,String centerMachineId)
			throws KeyManagementException, IOException, java.security.NoSuchAlgorithmException {
		LOGGER.debug("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID,
				getCenterId(getStationId(getMacAddress())));
		KeyStore keyStore = new KeyStore();
		List<ErrorResponseDTO> erResponseDTOs = new ArrayList<>();
		Map<String, String> requestParams = new HashMap<String, String>();
		requestParams.put(RegistrationConstants.TIME_STAMP,DateUtils.getUTCCurrentDateTimeString());
		requestParams.put(RegistrationConstants.REF_ID, centerMachineId);
		try {
			@SuppressWarnings("unchecked")
			PublicKeyResponse<String> publicKeyResponse = (PublicKeyResponse<String>) serviceDelegateUtil
					.get("policysync", requestParams, false, RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);

			if (null != publicKeyResponse.getResponse() && !publicKeyResponse.getResponse().isEmpty()
					&& publicKeyResponse.getResponse().size() > 0) {
  
				keyStore.setId(UUID.randomUUID().toString());
				keyStore.setPublicKey(publicKeyResponse.getResponse().get("publicKey").toString().getBytes());
				LocalDateTime issuedAt = DateUtils
						.parseToLocalDateTime(publicKeyResponse.getResponse().get("issuedAt").toString());
				LocalDateTime expiryAt = DateUtils
						.parseToLocalDateTime(publicKeyResponse.getResponse().get("expiryAt").toString());
				keyStore.setValidFromDtimes(Timestamp.valueOf(issuedAt));
				keyStore.setValidTillDtimes(Timestamp.valueOf(expiryAt));
				keyStore.setCreatedBy(getUserIdFromSession());
				keyStore.setCreatedDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
				keyStore.setRefId(centerMachineId);
				policySyncDAO.updatePolicy(keyStore);
				responseDTO = setSuccessResponse(responseDTO, RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE, null);
				LOGGER.info("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID,
						"synch the public key is completed");
			} else {
				List<LinkedHashMap<String, Object>> errorKey = publicKeyResponse.getErrors();
				ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
				errorResponseDTO.setCode(RegistrationConstants.ERRORS);
				errorResponseDTO.setMessage((String) errorKey.get(0).get(RegistrationConstants.ERROR_MSG));
				erResponseDTOs.add(errorResponseDTO);
				responseDTO.setErrorResponseDTOs(erResponseDTOs);
				LOGGER.info("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID,
						(String) errorKey.get(0).get(RegistrationConstants.ERROR_MSG));

			}

		} catch (HttpClientErrorException | RegBaseCheckedException exception) {
			LOGGER.error("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
			setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE, null);

		}

	}

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

			KeyStore keyStore = policySyncDAO.getPublicKey(getCenterId(getStationId(getMacAddress()))+"_"+getStationId(getMacAddress()));

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
				fetchPolicy(); 
			}
		} catch (RuntimeException runtimeException) {

			LOGGER.info("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
			setErrorResponse(responseDTO, RegistrationConstants.INVALID_KEY, null);
		}

		LOGGER.info("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID, "Key Validation is started");

		return responseDTO;

	}

}
