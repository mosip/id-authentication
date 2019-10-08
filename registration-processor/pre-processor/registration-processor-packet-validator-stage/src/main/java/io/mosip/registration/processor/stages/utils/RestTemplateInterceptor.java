package io.mosip.registration.processor.stages.utils;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auth.adapter.constant.AuthAdapterConstant;
import io.mosip.kernel.auth.adapter.model.AuthUserDetails;

/***********************************************************************************************************************
 * It is used to intercept any http calls made using rest template from this
 * application.
 *
 * CONFIG: This is added to the list of interceptors in the RestTemplate bean
 * created in the SecurityConfig.
 *
 * TASKS: 1. Intercept all the requests from the application and do the below
 * tasks. 2. Intercept a request to add auth token to the "Authorization"
 * header. 3. Intercept a response to modify the stored token with the
 * "Authorization" header of the response.
 *
 * @author Sabbu Uday Kumar
 * @author Ramadurai Saravana Pandian
 * @author Raj Jha 
 * @since 1.0.0
 **********************************************************************************************************************/

@Component
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
			ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
		addHeadersToRequest(httpRequest, bytes);
		ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
		// getHeadersFromResponse(response);
		return response;
	}

	private void addHeadersToRequest(HttpRequest httpRequest, byte[] bytes) {
		HttpHeaders headers = httpRequest.getHeaders();
		AuthUserDetails authUserDetails = getAuthUserDetails();
		if (authUserDetails != null)
			headers.set(AuthAdapterConstant.AUTH_HEADER_COOKIE,
					AuthAdapterConstant.AUTH_COOOKIE_HEADER + authUserDetails.getToken());
	}

	private AuthUserDetails getAuthUserDetails() {
		AuthUserDetails authUserDetails = null;
		if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof AuthUserDetails)

			authUserDetails = (AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return authUserDetails;
	}

	private void getHeadersFromResponse(ClientHttpResponse clientHttpResponse) {
		HttpHeaders headers = clientHttpResponse.getHeaders();
		String responseToken = headers.get(AuthAdapterConstant.AUTH_HEADER_SET_COOKIE).get(0)
				.replaceAll(AuthAdapterConstant.AUTH_COOOKIE_HEADER, "");
		getAuthUserDetails().setToken(responseToken);
	}

}