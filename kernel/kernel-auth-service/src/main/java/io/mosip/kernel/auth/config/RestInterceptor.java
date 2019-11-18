package io.mosip.kernel.auth.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.auth.dto.AccessTokenResponse;
import io.mosip.kernel.auth.util.MemoryCache;
import io.mosip.kernel.auth.util.TokenValidator;

/**
 * RestInterceptor for getting admin token
 * 
 * @author Urvil Joshi
 * @author Srinivasan
 *
 */
@Component
public class RestInterceptor implements ClientHttpRequestInterceptor {

	@Autowired
	private MemoryCache<String, AccessTokenResponse> memoryCache;
	
	@Autowired
	private TokenValidator tokenValidator;

	@Qualifier("authRestTemplate")
	@Autowired
	private RestTemplate restTemplate;

	@Value("${mosip.kernel.open-id-url}")
	private String keycloakOpenIdUrl;

	@Value("${mosip.master.realm-id}")
	private String realmId;

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		AccessTokenResponse accessTokenResponse = null;
		if ((accessTokenResponse = memoryCache.get("adminToken")) != null) {
			boolean accessTokenExpired=tokenValidator.isExpired(accessTokenResponse.getAccess_token());
			boolean refreshTokenExpired=tokenValidator.isExpired(accessTokenResponse.getRefresh_token());
			if (accessTokenExpired && refreshTokenExpired) {
				accessTokenResponse = getAdminToken(false, null);
			}else if(accessTokenExpired) {
				accessTokenResponse = getAdminToken(true, accessTokenResponse.getRefresh_token());
			}
		} else {
			accessTokenResponse = getAdminToken(false, null);
		}
		memoryCache.put("adminToken", accessTokenResponse);
		request.getHeaders().add("Authorization", "Bearer " + accessTokenResponse.getAccess_token());
		return execution.execute(request, body);
	}

	private AccessTokenResponse getAdminToken(boolean isGetRefreshToken, String refreshToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> tokenRequestBody = null;
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put("realmId", realmId);
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(keycloakOpenIdUrl + "/token");
		if (isGetRefreshToken) {
			tokenRequestBody = getAdminValueMap(refreshToken);
		} else {
			tokenRequestBody = getAdminValueMap();
		}

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(tokenRequestBody, headers);
		ResponseEntity<AccessTokenResponse> response = restTemplate.postForEntity(
				uriComponentsBuilder.buildAndExpand(pathParams).toUriString(), request, AccessTokenResponse.class);
		return response.getBody();
	}

	private MultiValueMap<String, String> getAdminValueMap() {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", "password");
		map.add("username", "admin");
		map.add("password", "KEYCLOAKPASS");
		map.add("client_id", "admin-cli");
		return map;
	}

	private MultiValueMap<String, String> getAdminValueMap(String refreshToken) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("grant_type", "refresh_token");
		map.add("refresh_token", refreshToken);
		map.add("client_id", "admin-cli");
		return map;
	}
}
