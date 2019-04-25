package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.LoggerConstants.REGISTRATION_PUBLIC_KEY_SYNC;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
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
import io.mosip.registration.dao.PolicySyncDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.KeyStore;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.PublicKeySync;

/**
 * The Interface for Public Key service implementation.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
@Service
public class PublicKeySyncImpl extends BaseService implements PublicKeySync {

	/** The policy sync DAO. */
	@Autowired
	private PolicySyncDAO policySyncDAO;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = AppConfig.getLogger(PublicKeySyncImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.PublicKeySync#getPublicKey()
	 */
	@Override
	synchronized public ResponseDTO getPublicKey(String triggerPoint) {

		LOGGER.info(REGISTRATION_PUBLIC_KEY_SYNC, APPLICATION_NAME, APPLICATION_ID,
				"Entering into get public key method.....");

		ResponseDTO responseDTO = null;

		try {

			LOGGER.info(REGISTRATION_PUBLIC_KEY_SYNC, APPLICATION_NAME, APPLICATION_ID,
					"Fetching signed public key.....");

			KeyStore keyStore = policySyncDAO.getPublicKey(RegistrationConstants.SIGNED_KEY);

			if (null == keyStore) {

				responseDTO = insertPublickey(triggerPoint);

				if (null != responseDTO && null != responseDTO.getSuccessResponseDTO()) {
					LOGGER.info(REGISTRATION_PUBLIC_KEY_SYNC, APPLICATION_NAME, APPLICATION_ID,
							responseDTO.getSuccessResponseDTO().getMessage());
				} else {
					LOGGER.info(REGISTRATION_PUBLIC_KEY_SYNC, APPLICATION_NAME, APPLICATION_ID,
							"Public Key Sync Failure");
				}

			} else {

				Date validDate = new Date(keyStore.getValidTillDtimes().getTime());
				long difference = ChronoUnit.DAYS.between(new Date().toInstant(), validDate.toInstant());

				if (difference <= 0) {

					responseDTO = insertPublickey(triggerPoint);

					if (null != responseDTO && null != responseDTO.getSuccessResponseDTO()) {
						LOGGER.info(REGISTRATION_PUBLIC_KEY_SYNC, APPLICATION_NAME, APPLICATION_ID,
								responseDTO.getSuccessResponseDTO().getMessage());
					} else {
						LOGGER.info(REGISTRATION_PUBLIC_KEY_SYNC, APPLICATION_NAME, APPLICATION_ID,
								"Public Key Sync Failure");
					}
				}
			}

		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error(REGISTRATION_PUBLIC_KEY_SYNC, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(regBaseCheckedException));
			responseDTO = setErrorResponse(new ResponseDTO(), regBaseCheckedException.getMessage(), null);
		}

		LOGGER.info(REGISTRATION_PUBLIC_KEY_SYNC, APPLICATION_NAME, APPLICATION_ID,
				"Leaving into get public key method.....");

		return responseDTO;
	}

	@SuppressWarnings("unchecked")
	private ResponseDTO insertPublickey(String triggerPoint) throws RegBaseCheckedException {

		LOGGER.info(REGISTRATION_PUBLIC_KEY_SYNC, APPLICATION_NAME, APPLICATION_ID,
				"Entering into insert public key method.....");

		ResponseDTO responseDTO = new ResponseDTO();
		Map<String, String> requestParamMap = new LinkedHashMap<>();
		requestParamMap.put(RegistrationConstants.REF_ID, RegistrationConstants.KER);
		requestParamMap.put(RegistrationConstants.TIME_STAMP, Instant.now().toString());

		try {

			LOGGER.info(REGISTRATION_PUBLIC_KEY_SYNC, APPLICATION_NAME, APPLICATION_ID,
					"Calling public key rest call.....");

			LinkedHashMap<String, Object> publicKeyResponse = (LinkedHashMap<String, Object>) serviceDelegateUtil
					.get(RegistrationConstants.PUBLIC_KEY_REST, requestParamMap, false, "system");

			System.out.println(publicKeyResponse);

			if (null != publicKeyResponse && publicKeyResponse.size() > 0
					&& null != publicKeyResponse.get(RegistrationConstants.PACKET_STATUS_READER_RESPONSE)) {

				LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) publicKeyResponse
						.get(RegistrationConstants.PACKET_STATUS_READER_RESPONSE);

				KeyStore keyStore = new KeyStore();

				keyStore.setId(UUID.randomUUID().toString());
				keyStore.setPublicKey(responseMap.get(RegistrationConstants.PUBLIC_KEY).toString().getBytes());
				LocalDateTime issuedAt = DateUtils
						.parseToLocalDateTime(responseMap.get(RegistrationConstants.PUBLIC_KEY_ISSUES_DATE).toString());
				LocalDateTime expiryAt = DateUtils
						.parseToLocalDateTime(responseMap.get(RegistrationConstants.PUBLIC_KEY_EXPIRE_DATE).toString());
				keyStore.setValidFromDtimes(Timestamp.valueOf(issuedAt));
				keyStore.setValidTillDtimes(Timestamp.valueOf(expiryAt));
				keyStore.setCreatedBy(getUserIdFromSession());
				keyStore.setRefId(RegistrationConstants.SIGNED_KEY);
				keyStore.setCreatedDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
				policySyncDAO.updatePolicy(keyStore);
				responseDTO = setSuccessResponse(responseDTO, RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE, null);
				LOGGER.info(REGISTRATION_PUBLIC_KEY_SYNC, APPLICATION_NAME, APPLICATION_ID,
						"Public key sync succesfull...");
			} else {

				responseDTO = setErrorResponse(responseDTO, publicKeyResponse.size() > 0
						? ((List<LinkedHashMap<String, String>>) publicKeyResponse.get(RegistrationConstants.ERRORS))
								.get(0).get(RegistrationConstants.ERROR_MSG)
						: "Public key Sync Restful service error", null);
				LOGGER.info(REGISTRATION_PUBLIC_KEY_SYNC, APPLICATION_NAME, APPLICATION_ID,
						(publicKeyResponse.size() > 0
								? ((List<LinkedHashMap<String, String>>) publicKeyResponse
										.get(RegistrationConstants.ERRORS)).get(0).get(RegistrationConstants.ERROR_MSG)
								: "Public key Sync Restful service error"));

			}

		} catch (HttpClientErrorException | SocketTimeoutException | RegBaseCheckedException reException) {

			LOGGER.error(REGISTRATION_PUBLIC_KEY_SYNC, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(reException));
			throw new RegBaseCheckedException("Exception in public key Rest Call", reException.getMessage());
		}

		LOGGER.info(REGISTRATION_PUBLIC_KEY_SYNC, APPLICATION_NAME, APPLICATION_ID,
				"Leaving into insert public key method.....");

		return responseDTO;

	}

}
