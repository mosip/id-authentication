package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class RegistrationAppLoginMethodID implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name = "app_id", length = 64, nullable = false, updatable = false)
	private String appId;
	@Column(name = "login_method", length = 64, nullable = false, updatable = false)
	private String loginMethod;
	@Column(name = "lang_code", length = 3, nullable = false, updatable = false)
	private String langCode;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getLoginMethod() {
		return loginMethod;
	}

	public void setLoginMethod(String loginMethod) {
		this.loginMethod = loginMethod;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

}
