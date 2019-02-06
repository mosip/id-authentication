package io.mosip.kernel.auth.adapter;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
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
 * 3. Updates token into AuthHeadersFilter.
 * 4. Bind MosipUserDto instance details with the AuthUserDetails that extends Spring Security's UserDetails.
 *
 * @author Sabbu Uday Kumar
 * @since 1.0.0
 **********************************************************************************************************************/

@Component
public class AuthProvider extends AbstractUserDetailsAuthenticationProvider {

    @Value("${auth.server.validate.url}")
    private String validateUrl;

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

    @Override
    protected UserDetails retrieveUser(String userName, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
        AuthToken authToken = (AuthToken) usernamePasswordAuthenticationToken;
        String token = authToken.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<MosipUserDto> response = null;
        try {
            response = getRestTemplate().exchange(validateUrl, HttpMethod.GET, entity, MosipUserDto.class);
        } catch (Exception err) {
            throw new RuntimeException("Invalid Token");
        }

        MosipUserDto mosipUserDto = response.getBody();
        if (mosipUserDto == null) {
            throw new RuntimeException("Invalid Token");
        }

        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(mosipUserDto.getRole());
        AuthUserDetails authUserDetails = new AuthUserDetails(mosipUserDto,
                response.getHeaders().get("Authorization").get(0));
        authUserDetails.setAuthorities(grantedAuthorities);

        return authUserDetails;
    }
}