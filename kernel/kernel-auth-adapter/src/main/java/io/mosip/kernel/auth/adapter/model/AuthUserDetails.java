package io.mosip.kernel.auth.adapter.model;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/***********************************************************************************************************************
 * Used by spring security to store user details like roles and use this across
 * the application for Authorization purpose. The user details can be fetched
 * using principal in SecurityContextHolder
 *
 * @author Sabbu Uday Kumar
 * @since 1.0.0
 **********************************************************************************************************************/

public class AuthUserDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4068560701182593212L;

	private String userId;
	private String token;
	private String mail;
	private String mobile;
	private String rId;
	

	private Collection<? extends GrantedAuthority> authorities;

	public AuthUserDetails(MosipUserDto mosipUserDto, String token) {
		this.userId = mosipUserDto.getUserId();
		this.token = token;
		this.mail = mosipUserDto.getMail();
		this.mobile = mosipUserDto.getMobile();
		this.rId=mosipUserDto.getRId();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getAuthority()))
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
		return userId;
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

	public String getUserId() {
		return userId;
	}

	public void setUserName(String userName) {
		this.userId = userName;
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
	public String getrId() {
		return rId;
	}

	public void setrId(String rId) {
		this.rId = rId;
	}
}