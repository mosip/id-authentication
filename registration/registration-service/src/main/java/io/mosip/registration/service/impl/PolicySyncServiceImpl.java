package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.PolicySyncDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.PolicyDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.KeyStore;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.PolicySyncService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

/**
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Service
public class PolicySyncServiceImpl implements PolicySyncService {

	@Autowired
	private PolicySyncDAO policySyncDAO;

	ObjectMapper objectMapper = new ObjectMapper();

	private static final Logger LOGGER = AppConfig.getLogger(PolicySyncServiceImpl.class);

	@Override
	public ResponseDTO fetchPolicy(String centerId) {
		LOGGER.debug("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID,
				"synch the public key is started");
		PolicyDTO policyDTO = null;
		String publicKey = null;
		KeyStore keyStore = null;
		ResponseDTO responseDTO = new ResponseDTO();
		SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();

		if (!RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
			responseDTO = buildErrorRespone(responseDTO, RegistrationConstants.POLICY_SYNC_CLIENT_NOT_ONLINE_ERROR_CODE,
					RegistrationConstants.POLICY_SYNC_CLIENT_NOT_ONLINE_ERROR_MESSAGE);
			return responseDTO;
		}

		keyStore = policySyncDAO.findByMaxExpireTime();
		if (keyStore != null) {
			Timestamp validTimestamp = keyStore.getValidTillDtimes();
			int threshold = 0;
			String value = (String) ApplicationContext.getInstance().getApplicationMap().get("name");
			threshold = Integer.parseInt(value);
			Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
			Date vdate = new Date(validTimestamp.getTime());
			Date cdate = new Date(currentTimestamp.getTime());
			long difference = (vdate.getTime() - cdate.getTime()) / 86400000;
			if (threshold < Math.abs(difference)) {
				successResponseDTO.setCode(RegistrationConstants.POLICY_SYNC_SUCCESS_CODE);
				successResponseDTO.setMessage(RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE);
				successResponseDTO.setInfoType(RegistrationConstants.ALERT_INFORMATION);
				responseDTO.setSuccessResponseDTO(successResponseDTO);
				return responseDTO;
			}
		}
		// make the rest call and get the data like as below.
		String data = "{\r\n" + "  \"publicKey\": \"publicKey\",\r\n" + "  \"keyExpiryTime\": \"2018-12-29\",\r\n"
				+ "  \"keyGenerationTime\": \"2018-11-29\"\r\n" + "}";

		try {
			policyDTO = objectMapper.readValue(data, PolicyDTO.class);
			keyStore = new KeyStore();
			keyStore.setId("id");
			keyStore.setPublicKey(policyDTO.getPublicKey().getBytes());
			keyStore.setValidFromDtimes(policyDTO.getKeyGenerationTime());
			keyStore.setValidTillDtimes(policyDTO.getKeyExpiryTime());
			keyStore.setCreatedBy("createdBy");
			keyStore.setCreatedDtimes(new Timestamp(System.currentTimeMillis()));
			keyStore.setUpdatedBy("updatedBy");
			keyStore.setUpdatedTimes(new Timestamp(System.currentTimeMillis()));

			policySyncDAO.updatePolicy(keyStore);
			successResponseDTO.setCode(RegistrationConstants.POLICY_SYNC_SUCCESS_CODE);
			successResponseDTO.setMessage(RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE);
			successResponseDTO.setInfoType(RegistrationConstants.ALERT_INFORMATION);
			responseDTO.setSuccessResponseDTO(successResponseDTO);
			LOGGER.debug("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID, "synch is success");

		} catch (IOException|RegBaseUncheckedException e) {

			responseDTO = buildErrorRespone(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_CODE,
					RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE);
			LOGGER.debug("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID, "error response is created");
		}
		return responseDTO;
	}

	private ResponseDTO buildErrorRespone(ResponseDTO response, final String errorCode, final String message) {
		/* Create list of Error Response */
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
