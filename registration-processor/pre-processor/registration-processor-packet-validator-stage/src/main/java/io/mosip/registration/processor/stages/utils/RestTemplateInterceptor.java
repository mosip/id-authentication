package io.mosip.registration.processor.stages.utils;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.rest.client.utils.RestApiClient;

/***********************************************************************************************************************
 * It is used to intercept any http calls made using rest template from this
 * application.
 *
 * @author Mukul Puspam
 **********************************************************************************************************************/

@Component
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

	RestApiClient restApiClient;
	private static final String AUTH_HEADER_COOKIE = "Cookie";
	private static final String AUTH_COOOKIE_HEADER = "Authorization=";

	public RestTemplateInterceptor(RestApiClient restApiClient) {
		this.restApiClient = restApiClient;
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
			ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
		addHeadersToRequest(httpRequest, bytes);
		ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
		return response;
	}

	private void addHeadersToRequest(HttpRequest httpRequest, byte[] bytes) throws IOException {
		HttpHeaders headers = httpRequest.getHeaders();
		String accessToken = restApiClient.getToken().substring(14);
		if (accessToken != null)
			headers.set(AUTH_HEADER_COOKIE, AUTH_COOOKIE_HEADER + accessToken);
	}

}