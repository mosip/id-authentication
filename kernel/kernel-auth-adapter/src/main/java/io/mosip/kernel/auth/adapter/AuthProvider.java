package io.mosip.kernel.auth.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/***********************************************************************************************************************
 * Contacts auth server to verify token validity.
 *
 * Tasks:
 * 1. Contacts auth server to verify token validity.
 * 2. Stores the response body in an instance of MosipUser.
 * 3. Updates token into AuthHeadersFilter.
 * 4. Bind MosipUser instance details with the AuthUserDetails that extends Spring Security's UserDetails.
 **********************************************************************************************************************/

@Component
public class AuthProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    AuthHeadersFilter authHeadersFilter;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(String userName, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
        AuthToken authToken = (AuthToken) usernamePasswordAuthenticationToken;
        String token = authToken.getToken();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<MosipUser> response = null;
        try {
            response = restTemplate.exchange("http://localhost:5000/validate_token", HttpMethod.GET, entity, MosipUser.class);
        } catch (Exception err) {
            throw new RuntimeException("Invalid Token");
        }

        MosipUser mosipUser = response.getBody();
        authHeadersFilter.setToken(response.getHeaders().get("Authorization").get(0));
        if (mosipUser == null) {
            throw new RuntimeException("Invalid Token");
        }

        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(mosipUser.getRole());

        AuthUserDetails authUserDetails = new AuthUserDetails();
        authUserDetails.setUserName(mosipUser.getUserName());
        authUserDetails.setToken(token);
        authUserDetails.setAuthorities(grantedAuthorities);

        return authUserDetails;
    }
}
