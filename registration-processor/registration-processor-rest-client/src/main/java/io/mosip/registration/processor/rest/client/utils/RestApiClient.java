package io.mosip.registration.processor.rest.client.utils;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;

/**
 * The Class RestApiClient.
 * 
 * @author Rishabh Keshari
 */
@Component
public class RestApiClient {

	/** The logger. */
	private final Logger logger = RegProcessorLogger.getLogger(RestApiClient.class);

	/** The builder. */
	@Autowired
	RestTemplateBuilder builder;

	@Autowired
	Environment environment;

	/**
	 * Gets the api.
	 *
	 * @param <T>
	 *            the generic type
	 * @param getURI
	 *            the get URI
	 * @param responseType
	 *            the response type
	 * @return the api
	 */
	public <T> T getApi(String getURI, Class<?> responseType) {
		RestTemplate restTemplate;
		try {
			restTemplate = getRestTemplate();
			T result = (T) restTemplate.getForObject(getURI, responseType);

			return result;
		} catch (Exception e) {
			logger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getMessage()+ExceptionUtils.getStackTrace(e));

		}
		return null;
	}

	/**
	 * Post api.
	 *
	 * @param <T>
	 *            the generic type
	 * @param uri
	 *            the uri
	 * @param requestType
	 *            the request type
	 * @param responseClass
	 *            the response class
	 * @return the t
	 */
	public <T> T postApi(String uri, Object requestType, Class<?> responseClass) throws Exception {

		RestTemplate restTemplate;
		T result = null;
		try {
			restTemplate = getRestTemplate();
			logger.info(uri);
			logger.info(requestType.toString());
			result = (T) restTemplate.postForObject(uri, requestType, responseClass);
		} catch (Exception e) {

			logger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getMessage()+ExceptionUtils.getStackTrace(e));

			throw e;
		}
		return result;
	}

	public RestTemplate getRestTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		logger.info(Arrays.asList(environment.getActiveProfiles()).toString());
		if (Arrays.stream(environment.getActiveProfiles()).anyMatch("dev-k8"::equals)) {
			logger.info(Arrays.asList(environment.getActiveProfiles()).toString());
			return new RestTemplate();
		} else {
			TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

			SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
					.loadTrustMaterial(null, acceptingTrustStrategy).build();

			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

			CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();

			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

			requestFactory.setHttpClient(httpClient);
			return new RestTemplate(requestFactory);
		}

	}

}
