package io.mosip.kernel.auth.adapter;

import org.apache.http.client.HttpResponseException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;

/***********************************************************************************************************************
 * Contacts auth server to verify token validity.
 *
 * Tasks:
 * 1. Contacts auth server to verify token validity.
 * 2. Stores the response body in an instance of MosipUserDto.
 * 3. Updates token into in the security context through AuthUserDetails.
 * 4. Bind MosipUserDto instance details with the AuthUserDetails that extends Spring Security's UserDetails.
 *
 * @author Sabbu Uday Kumar
 * @since 1.0.0
 **********************************************************************************************************************/

@Component
public class AuthProvider extends AbstractUserDetailsAuthenticationProvider {

    @Value("${auth.server.validate.url}")
    private String validateUrl;
    
    @Value("${auth.server.refreshToken.url}")
    private String refreshTokenUrl;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
    }

    private RestTemplate getRestTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }

	private ResponseEntity<MosipUserDto> getResponseEntity(
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken, String newToken) throws RestClientException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
		String token =null;
		AuthToken authToken = (AuthToken) usernamePasswordAuthenticationToken;
		if(newToken!=null)
		{
			token = newToken;
		}
		else
		{
			token = authToken.getToken();
		}
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", AuthAdapterConstant.AUTH_COOOKIE_HEADER+token);
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);           
        ResponseEntity<MosipUserDto> response = getRestTemplate().exchange(validateUrl, HttpMethod.POST, entity, MosipUserDto.class);
		return response;
	}
	
	private ResponseEntity<MosipUserDto> getNewToken(String token) throws RestClientException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
		HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", AuthAdapterConstant.AUTH_COOOKIE_HEADER+token);
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);           
        ResponseEntity<MosipUserDto> response = getRestTemplate().exchange(refreshTokenUrl, HttpMethod.POST, entity, MosipUserDto.class);
        return response;
	}
    
    @Override
    protected UserDetails retrieveUser(String userName, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
    	ResponseEntity<MosipUserDto> response = null;
        try {
        	  response =  getResponseEntity(usernamePasswordAuthenticationToken,null);
        } catch (NonceExpiredException expired) {
        	
        }catch (HttpClientErrorException | HttpServerErrorException  | KeyManagementException | KeyStoreException | NoSuchAlgorithmException  err) {

        	AuthToken authToken = (AuthToken) usernamePasswordAuthenticationToken;
        	try {
				response = getNewToken(authToken.getToken());
			} catch (RestClientException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException e) {
				throw new RuntimeException("Failure of Token");
			}
        
        }
        
        MosipUserDto mosipUserDto = response.getBody();
        if (mosipUserDto == null) {
            throw new RuntimeException("Invalid Token");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(mosipUserDto.getRole());
        String responseToken = response.getHeaders().get("Set-Cookie").get(0).replaceAll(AuthAdapterConstant.AUTH_COOOKIE_HEADER, "");
        AuthUserDetails authUserDetails = new AuthUserDetails(mosipUserDto,responseToken);
        authUserDetails.setAuthorities(grantedAuthorities);
        return authUserDetails;
    }

	


}