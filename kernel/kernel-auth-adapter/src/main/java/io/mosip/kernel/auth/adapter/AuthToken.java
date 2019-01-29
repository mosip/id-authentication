package io.mosip.kernel.auth.adapter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.HashMap;

/***********************************************************************************************************************
 * AUTH_TOKEN USED TO STORE TOKEN DETAILS
 **********************************************************************************************************************/

public class AuthToken extends UsernamePasswordAuthenticationToken {

    private String token;
//    private HashMap principal;

    public AuthToken() {
        super(null, null);
    }

    public AuthToken(String token) {
        super(null, null);
        this.token = token;
//        this.principal = principal;

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

//    @Override
//    public HashMap getPrincipal() {
//        return principal;
//    }

    @Override
    public HashMap getPrincipal() {
        return null;
    }
//
//    @Override
//    public String getName() {
//        return principal.get("userName").toString();
//    }
}
