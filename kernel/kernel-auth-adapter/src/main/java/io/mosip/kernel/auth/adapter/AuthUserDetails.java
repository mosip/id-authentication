package io.mosip.kernel.auth.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/***********************************************************************************************************************
 * Used by spring security to store user details like roles and use this across the application for Authorization purpose.
 **********************************************************************************************************************/

public class AuthUserDetails implements UserDetails {

    @Value("${auth.role.prefix}")
    private String rolePrefix;

    private String userName;
    private String token;
    private String mail;
    private String mobile;
    private Collection<? extends GrantedAuthority> authorities;

    public AuthUserDetails(MosipUser mosipUser, String token) {
        this.userName = mosipUser.getUserName();
        this.token = token;
        this.mail = mosipUser.getMail();
        this.mobile = mosipUser.getMobile();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities
                .stream()
                .map(role -> new SimpleGrantedAuthority(rolePrefix + role.getAuthority()))
                .collect(Collectors.toList());
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
