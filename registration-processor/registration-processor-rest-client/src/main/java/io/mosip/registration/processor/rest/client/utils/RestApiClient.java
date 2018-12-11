package io.mosip.registration.processor.rest.client.utils;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * The Class RestApiClient.
 * 
 * @author Rishabh Keshari
 */
@Component
public class RestApiClient{

	/** The logger. */
	private final static Logger LOGGER = LoggerFactory.getLogger(RestApiClient.class);

	@Autowired
	RestTemplateBuilder builder;
	/**
	 * Gets the api.
	 *
	 * @param <T> the generic type
	 * @param getURI the get URI
	 * @param responseType the response type
	 * @return the api
	 */
	@SuppressWarnings("unchecked")
	public <T> T getApi(String getURI , Class<?> responseType) {
		RestTemplate restTemplate;
		T result=null;
		try {
			restTemplate = getRestTemplate();
			result = (T) restTemplate.getForObject(getURI, responseType);
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return result;
	}

	/**
	 * Post api.
	 *
	 * @param <T> the generic type
	 * @param uri the uri
	 * @param requestType the request type
	 * @param responseClass the response class
	 * @return the t
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public <T> T postApi(String uri, T requestType,Class<?> responseClass)  {

		RestTemplate restTemplate;
		T result=null;
		try {
			restTemplate = getRestTemplate();
			result = (T) restTemplate.postForObject(uri, requestType, responseClass);
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return result;
	}


	public static RestTemplate getRestTemplate() throws Exception {
		SSLContext sslContext = SSLContext.getInstance("SSL");
		// set up a TrustManager that trusts everything
		sslContext.init(null, new TrustManager[] { new X509TrustManager
				() {
			public X509Certificate[] getAcceptedIssuers() {
				LOGGER.info("getAcceptedIssuers =============");
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs,
					String authType) {
				LOGGER.info("checkClientTrusted =============");
			}

			public void checkServerTrusted(X509Certificate[] certs,
					String authType) {
				LOGGER.info("checkServerTrusted =============");
			}
		} }, new SecureRandom());

		SSLConnectionSocketFactory sslcon=new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpClient=HttpClients.custom().setSSLSocketFactory(sslcon).build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

		requestFactory.setHttpClient(httpClient);
		return new RestTemplate(requestFactory);
	}

}
