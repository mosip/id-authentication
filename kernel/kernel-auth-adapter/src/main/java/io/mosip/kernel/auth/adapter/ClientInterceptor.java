package io.mosip.kernel.auth.adapter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

/***********************************************************************************************************************
 * It is used to intercept any http calls made using rest template from this application.
 *
 * CONFIG:
 * This is added to the list of interceptors in the RestTemplate bean created in the SecurityConfig.
 *
 * TASKS:
 * 1. Intercept all the requests from the application and do the below tasks.
 * 2. Intercept a request to add auth token to the "Authorization" header.
 * 3. Intercept a response to modify the stored token with the "Authorization" header of the response.
 *
 * @author Sabbu Uday Kumar
 * @since 1.0.0
 **********************************************************************************************************************/

@Component
public class ClientInterceptor implements ClientHttpRequestInterceptor {

    private AuthUserDetails getAuthUserDetails() {
        AuthUserDetails authUserDetails = (AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authUserDetails;
    }

    private void addHeadersToRequest(HttpRequest httpRequest, byte[] bytes) {
        HttpHeaders headers = httpRequest.getHeaders();
        headers.set("Authorization", getAuthUserDetails().getToken());
    }

    private void getHeadersFromResponse(ClientHttpResponse clientHttpResponse) {
        HttpHeaders headers = clientHttpResponse.getHeaders();
        getAuthUserDetails().setToken(headers.get("Authorization").get(0));
//        getAuthUserDetails().setToken("sabbu");
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        addHeadersToRequest(httpRequest, bytes);
        ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
        getHeadersFromResponse(response);
        return response;
    }
}