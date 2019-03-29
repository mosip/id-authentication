package io.mosip.kernel.auth.adapter;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;

/***********************************************************************************************************************
 * Contacts auth server to verify token validity.
 *
 * Tasks: 1. Contacts auth server to verify token validity. 2. Stores the
 * response body in an instance of MosipUserDto. 3. Updates token into in the
 * security context through AuthUserDetails. 4. Bind MosipUserDto instance
 * details with the AuthUserDetails that extends Spring Security's UserDetails.
 *
 * @author Sabbu Uday Kumar
 * @since 1.0.0
 **********************************************************************************************************************/

@Component
public class AuthProvider extends AbstractUserDetailsAuthenticationProvider {

	@Value("${auth.server.validate.url}")
	private String validateUrl;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
	}

	private RestTemplate getRestTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
				.build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}

	private ResponseEntity<String> getResponseEntity(
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken)
			throws RestClientException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		String token = null;
		AuthToken authToken = (AuthToken) usernamePasswordAuthenticationToken;
		token = authToken.getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.set(AuthAdapterConstant.AUTH_HEADER_COOKIE, AuthAdapterConstant.AUTH_COOOKIE_HEADER + token);
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		ResponseEntity<String> response = getRestTemplate().exchange(validateUrl, HttpMethod.POST, entity,
				String.class);
		return response;
	}

	@Override
	protected UserDetails retrieveUser(String userName,
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
		ResponseEntity<String> response = null;
		MosipUserDto mosipUserDto = null;
		try {
			response = getResponseEntity(usernamePasswordAuthenticationToken);
			List<ServiceError> validationErrorsList = null;
			validationErrorsList = ExceptionUtils.getServiceErrorList(response.getBody());
        
		if (!validationErrorsList.isEmpty()) {
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),"Exception Occured");
		}
		}
		catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException exp) {
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), exp.getMessage());
		}
		try {
			mosipUserDto = objectMapper.readValue(response.getBody(), MosipUserDto.class);
		} catch (Exception e) {
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), e.getMessage());
		}
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils
				.commaSeparatedStringToAuthorityList(mosipUserDto.getRole());
		String responseToken = response.getHeaders().get(AuthAdapterConstant.AUTH_HEADER_SET_COOKIE).get(0)
				.replaceAll(AuthAdapterConstant.AUTH_COOOKIE_HEADER, "");
		AuthUserDetails authUserDetails = new AuthUserDetails(mosipUserDto, responseToken);
		authUserDetails.setAuthorities(grantedAuthorities);
		return authUserDetails;
	}
}