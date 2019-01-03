package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.cert.CertificateException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.PolicySyncDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.PublicKeyResponse;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.KeyStore;
import io.mosip.registration.service.PolicySyncService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

/**
 * it does the key policy synch
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Service
public class PolicySyncServiceImpl implements PolicySyncService {
	@Value("${policysync.service.url}")
	private String url;
	@Autowired
	private PolicySyncDAO policySyncDAO;

	private static final Logger LOGGER = AppConfig.getLogger(PolicySyncServiceImpl.class);

	KeyStore keyStore = null;
	ResponseDTO responseDTO = new ResponseDTO();
	SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.PolicySyncService#fetchPolicy(centerId)
	 */
	@Override
	public ResponseDTO fetchPolicy(String centerId) {
		LOGGER.debug("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID,
				"synch the public key is started");

		if (!RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
			responseDTO = buildErrorRespone(responseDTO, RegistrationConstants.POLICY_SYNC_CLIENT_NOT_ONLINE_ERROR_CODE,
					RegistrationConstants.POLICY_SYNC_CLIENT_NOT_ONLINE_ERROR_MESSAGE);
		} else {
			keyStore = policySyncDAO.findByMaxExpireTime();
			if (keyStore != null) {

				Date validDate = new Date(keyStore.getValidTillDtimes().getTime());
				long difference = ChronoUnit.DAYS.between(new Date().toInstant(), validDate.toInstant());
				if (Integer.parseInt((String) ApplicationContext.getInstance().getApplicationMap()
						.get(RegistrationConstants.KEY_NAME)) < difference) {
					successResponseDTO.setCode(RegistrationConstants.POLICY_SYNC_SUCCESS_CODE);
					successResponseDTO.setMessage(RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE);
					successResponseDTO.setInfoType(RegistrationConstants.ALERT_INFORMATION);
					responseDTO.setSuccessResponseDTO(successResponseDTO);
				} else {

					try {
						getPublicKey(LocalDateTime.now(), RegistrationConstants.REFERENCE_ID);
					} catch (KeyManagementException | IOException | java.security.NoSuchAlgorithmException e) {

						responseDTO = buildErrorRespone(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_CODE,
								RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE);
						LOGGER.error("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID,
								"error response is created");

					}
				}
			} else {
				try {
					getPublicKey(LocalDateTime.now(), RegistrationConstants.REFERENCE_ID);
				} catch (KeyManagementException | IOException | java.security.NoSuchAlgorithmException e) {
					responseDTO = buildErrorRespone(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_CODE,
							RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE);
					LOGGER.error("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID,
							"error response is created");

				}

			}
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

	public void getPublicKey(LocalDateTime timeStamp, String referenceId)
			throws KeyManagementException, IOException, java.security.NoSuchAlgorithmException {

		KeyStore keyStore = new KeyStore();

		PublicKeyResponse<String> publicKeyResponse = null;
		turnOffSslChecking();
		RestTemplate restTemplate = new RestTemplate();
		Map<String, Object> uriVariables = new HashMap<String, Object>();
		uriVariables.put("applicationId", "REGISTRATION");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<Object> entity = new HttpEntity<Object>(headers);

		URI uri = UriComponentsBuilder.fromUriString(url).buildAndExpand(uriVariables).toUri();
		uri = UriComponentsBuilder.fromUri(uri).queryParam("timeStamp", timeStamp)
				.queryParam("referenceId", referenceId.toString()).build().toUri();

		ResponseEntity<PublicKeyResponse> exchange = restTemplate.exchange(uri, HttpMethod.GET, entity,
				PublicKeyResponse.class);
		publicKeyResponse = exchange.getBody();
		try {

			keyStore.setId(UUID.randomUUID().toString());
			keyStore.setPublicKey(((String) publicKeyResponse.getPublicKey()).getBytes());
			keyStore.setValidFromDtimes(Timestamp.valueOf(publicKeyResponse.getIssuedAt()));
			keyStore.setValidTillDtimes(Timestamp.valueOf(publicKeyResponse.getExpiryAt()));
			keyStore.setCreatedBy("brahma");
			keyStore.setCreatedDtimes(new Timestamp(System.currentTimeMillis()));
			keyStore.setUpdatedBy("brahma");
			keyStore.setUpdatedTimes(new Timestamp(System.currentTimeMillis()));
			policySyncDAO.updatePolicy(keyStore);
			successResponseDTO.setCode(RegistrationConstants.POLICY_SYNC_SUCCESS_CODE);
			successResponseDTO.setMessage(RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE);
			successResponseDTO.setInfoType(RegistrationConstants.ALERT_INFORMATION);
			responseDTO.setSuccessResponseDTO(successResponseDTO);
			LOGGER.debug("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID, "synch is success");
		} catch (NoSuchAlgorithmException exception) {

			responseDTO = buildErrorRespone(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_CODE,
					RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE);
			LOGGER.error("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID, "error response is created");

		} catch (RuntimeException exception) {

			responseDTO = buildErrorRespone(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_CODE,
					RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE);
			LOGGER.error("REGISTRATION_KEY_POLICY_SYNC", APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
		}

	}

	private static final TrustManager[] UNQUESTIONING_TRUST_MANAGER = new TrustManager[] { new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
				throws CertificateException {
			// TODO Auto-generated method stub

		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
				throws CertificateException {
			// TODO Auto-generated method stub

		}
	} };

	public static void turnOffSslChecking() throws KeyManagementException, java.security.NoSuchAlgorithmException {
		// Install the all-trusting trust manager
		final SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, UNQUESTIONING_TRUST_MANAGER, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

}
