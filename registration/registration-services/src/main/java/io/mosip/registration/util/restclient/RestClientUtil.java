package io.mosip.registration.util.restclient;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * This is a general method which gives the response for all httpmethod
 * designators.
 *
 * @author Yaswanth S
 * @since 1.0.0
 */
@Service
public class RestClientUtil {

	/**
	 * Rest Template is a interaction with HTTP servers and enforces RESTful systems
	 */
	@Autowired
	RestTemplate restTemplate;

	private static final Logger LOGGER = AppConfig.getLogger(RestClientUtil.class);

	/**
	 * Actual exchange using rest template.
	 *
	 * @param requestHTTPDTO 
	 * 				the request HTTPDTO
	 * @return ResponseEntity 
	 * 				response entity obtained from api
	 * @throws RegBaseCheckedException 
	 * 				the reg base checked exception
	 * @throws HttpClientErrorException 
	 * 				when client error exception from server
	 * @throws HttpServerErrorException 
	 * 				when server exception from server
	 * @throws SocketTimeoutException 
	 * 				the socket timeout exception
	 * @throws ResourceAccessException 
	 * 				the resource access exception
	 */
	public Map<String, Object> invoke(RequestHTTPDTO requestHTTPDTO)
			throws RegBaseCheckedException, SocketTimeoutException, ResourceAccessException {
		LOGGER.debug("REGISTRATION - REST_CLIENT_UTIL - INVOKE", APPLICATION_NAME, APPLICATION_ID,
				"invoke method called");

		return invokeURL(requestHTTPDTO);
	}

	private Map<String, Object> invokeURL(RequestHTTPDTO requestHTTPDTO) {
		ResponseEntity<?> responseEntity = null;
		Map<String, Object> responseMap = null;
		restTemplate.setRequestFactory(requestHTTPDTO.getSimpleClientHttpRequestFactory());
		//To-do need to be removed after checking this properly
		try {
			if (requestHTTPDTO.getUri().toString().contains("https"))
				turnOffSslChecking();
		} catch (KeyManagementException keyManagementException) {
			LOGGER.error("REGISTRATION - REST_CLIENT_UTIL - INVOKE", APPLICATION_NAME, APPLICATION_ID,
					keyManagementException.getMessage() + ExceptionUtils.getStackTrace(keyManagementException));
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			LOGGER.error("REGISTRATION - REST_CLIENT_UTIL - INVOKE", APPLICATION_NAME, APPLICATION_ID,
					noSuchAlgorithmException.getMessage() + ExceptionUtils.getStackTrace(noSuchAlgorithmException));
		}
		
		LOGGER.info("REGISTRATION - REST_CLIENT_UTIL - INVOKE", APPLICATION_NAME, APPLICATION_ID,
				"Request URL======>" + requestHTTPDTO.getUri());
		LOGGER.info("REGISTRATION - REST_CLIENT_UTIL - INVOKE", APPLICATION_NAME, APPLICATION_ID,
				"Request Method======>" + requestHTTPDTO.getHttpMethod()); 
		
		if (!StringUtils.containsIgnoreCase(requestHTTPDTO.getUri().toString(),
				RegistrationConstants.AUTH_SERVICE_URL)) {
			LOGGER.info("REGISTRATION - REST_CLIENT_UTIL - INVOKE", APPLICATION_NAME, APPLICATION_ID,
					"Request Entity======>" + requestHTTPDTO.getHttpEntity());
			LOGGER.info("REGISTRATION - REST_CLIENT_UTIL - INVOKE", APPLICATION_NAME, APPLICATION_ID,
					"Request Class======>" + requestHTTPDTO.getClazz());
		}

		responseEntity = restTemplate.exchange(requestHTTPDTO.getUri(), requestHTTPDTO.getHttpMethod(),
				requestHTTPDTO.getHttpEntity(), requestHTTPDTO.getClazz());
		
		if (responseEntity != null && responseEntity.hasBody()) {
			responseMap = new LinkedHashMap<>();
				LOGGER.info("REGISTRATION - REST_CLIENT_UTIL - INVOKE", APPLICATION_NAME, APPLICATION_ID,
						"Response Body======>" + responseEntity.getBody());
				LOGGER.info("REGISTRATION - REST_CLIENT_UTIL - INVOKE", APPLICATION_NAME, APPLICATION_ID,
						"Response Header======>" + responseEntity.getHeaders());
			responseMap.put(RegistrationConstants.REST_RESPONSE_BODY, responseEntity.getBody());
			responseMap.put(RegistrationConstants.REST_RESPONSE_HEADERS, responseEntity.getHeaders());
		}

		LOGGER.debug("REGISTRATION - REST_CLIENT_UTIL - INVOKE", APPLICATION_NAME, APPLICATION_ID,
				"invoke method ended");
		return responseMap;
	}
	
	/**
	 * Actual exchange using rest template.
	 *
	 * @param requestHTTPDTO 
	 * 				the request HTTPDTO
	 * @return ResponseEntity 
	 * 				response entity obtained from api
	 * @throws RegBaseCheckedException 
	 * 				the reg base checked exception
	 * @throws HttpClientErrorException 
	 * 				when client error exception from server
	 * @throws HttpServerErrorException 
	 * 				when server exception from server
	 * @throws SocketTimeoutException 
	 * 				the socket timeout exception
	 * @throws ResourceAccessException 
	 * 				the resource access exception
	 */
	public Map<String, Object> invokeForToken(RequestHTTPDTO requestHTTPDTO)
			throws RegBaseCheckedException, SocketTimeoutException, ResourceAccessException {
		LOGGER.debug("REGISTRATION - REST_CLIENT_UTIL - INVOKE Token", APPLICATION_NAME, APPLICATION_ID,
				"invoke token method called"); 
		
		return invokeURL(requestHTTPDTO);
	}

	/**
	 * Turn off ssl checking.
	 *
	 * @throws NoSuchAlgorithmException 
	 * 				the no such algorithm exception
	 * @throws KeyManagementException 
	 * 				the key management exception
	 */
	public static void turnOffSslChecking() throws NoSuchAlgorithmException, KeyManagementException {
		// Install the all-trusting trust manager
		final SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, UNQUESTIONING_TRUST_MANAGER, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	/** The Constant UNQUESTIONING_TRUST_MANAGER. */
	public static final TrustManager[] UNQUESTIONING_TRUST_MANAGER = new TrustManager[] { new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		/* (non-Javadoc)
		 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
		 */
		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws java.security.cert.CertificateException {
			// TODO Auto-generated method stub

		}

		/* (non-Javadoc)
		 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
		 */
		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws java.security.cert.CertificateException {
			// TODO Auto-generated method stub

		}
	} };

}
