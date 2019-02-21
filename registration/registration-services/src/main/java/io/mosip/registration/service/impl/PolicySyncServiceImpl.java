package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.security.KeyManagementException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.PolicySyncDAO;
import io.mosip.registration.dto.PublicKeyResponse;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.KeyStore;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.PolicySyncService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

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
	private ServiceDelegateUtil serviceDelegateUtil;
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
		LOGGER.info("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID,
				"synch the public key is started");

		KeyStore keyStore = null;
		ResponseDTO responseDTO = new ResponseDTO();
		if (!RegistrationAppHealthCheckUtil.isNetworkAvailable()) {

			setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_CLIENT_NOT_ONLINE_ERROR_MESSAGE, null);
		} else {
			keyStore = policySyncDAO.findByMaxExpireTime();

			if (keyStore != null) {
				Date validDate = new Date(keyStore.getValidTillDtimes().getTime());
				long difference = ChronoUnit.DAYS.between(new Date().toInstant(), validDate.toInstant());
				if (Integer
						.parseInt((String) ApplicationContext.map().get(RegistrationConstants.KEY_NAME)) < difference) {
					setSuccessResponse(responseDTO, RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE, null);
				} else {

					try {
						getPublicKey();
					} catch (KeyManagementException | IOException | java.security.NoSuchAlgorithmException e) {
						LOGGER.error("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID,
								"error response is created");

						setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE, null);

					}
				}
			} else {
				try {
					getPublicKey();
				} catch (KeyManagementException | IOException | java.security.NoSuchAlgorithmException exception) {
					LOGGER.error("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID,
							exception.getMessage());
					setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE, null);

				}

			}
		}
		return responseDTO;
	}

	public synchronized void getPublicKey()
			throws KeyManagementException, IOException, java.security.NoSuchAlgorithmException {

		KeyStore keyStore = new KeyStore();
		ResponseDTO responseDTO = new ResponseDTO();
		Map<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("timeStamp", Instant.now().toString());
		requestParams.put("referenceId", getCenterId(getStationId(getMacAddress())));
		try {
			PublicKeyResponse<String> publicKeyResponse = (PublicKeyResponse<String>) serviceDelegateUtil
					.get("policysync", requestParams, false);
			keyStore.setId(UUID.randomUUID().toString());
			keyStore.setPublicKey(((String) publicKeyResponse.getPublicKey()).getBytes());
			keyStore.setValidFromDtimes(Timestamp.valueOf(publicKeyResponse.getIssuedAt()));
			keyStore.setValidTillDtimes(Timestamp.valueOf(publicKeyResponse.getExpiryAt()));
			keyStore.setCreatedBy(getUserIdFromSession());
			keyStore.setCreatedDtimes(Timestamp.valueOf(LocalDateTime.now()));
			policySyncDAO.updatePolicy(keyStore);
			setSuccessResponse(responseDTO, RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE, null);

		} catch (HttpClientErrorException | RegBaseCheckedException exception) {
			LOGGER.error("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
			setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE, null);

		}

	}
}
