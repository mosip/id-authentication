package io.mosip.registration.processor.rest.client.utils;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
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
public class RestApiClient {

	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(RestApiClient.class);

	@Autowired
	RestTemplateBuilder builder;

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

			logger.error(e.getMessage());
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
	 * @throws Exception
	 */
	public <T> T postApi(String uri, T requestType, Class<?> responseClass) {

		RestTemplate restTemplate;
		try {
			System.out.println("Your URl is to change ::   " + uri);
			restTemplate = getRestTemplate();

			T result = (T) restTemplate.postForObject(uri, requestType, responseClass);
			return result;
		} catch (Exception e) {

			logger.error(e.getMessage());
		}
		return null;
	}

	public static RestTemplate getRestTemplate() throws Exception {
		SSLContext sslContext = SSLContext.getInstance("SSL");
		// set up a TrustManager that trusts everything
		sslContext.init(null, new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {

				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {

			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {

			}
		} }, new SecureRandom());

		SSLSocketFactory sf = new SSLSocketFactory(sslContext);

		Scheme httpsScheme = new Scheme("https", 443, (SchemeSocketFactory) sf);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(httpsScheme);

		// apache HttpClient version >4.2 should use BasicClientConnectionManager
		ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);
		HttpClient httpClient = new DefaultHttpClient(cm);
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

		requestFactory.setHttpClient(httpClient);

		return new RestTemplate(requestFactory);
	}

}
