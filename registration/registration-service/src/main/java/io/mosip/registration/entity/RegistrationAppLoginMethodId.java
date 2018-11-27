package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Composite key for RegistrationAppLoginMethod entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Embeddable
public class RegistrationAppLoginMethodId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "app_id")
	private String appId;
	@Column(name = "login_method_code")
	private String loginMethod;
	@Column(name = "lang_code")
	private String langCode;

	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId
	 *            the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * @return the loginMethod
	 */
	public String getLoginMethod() {
		return loginMethod;
	}

	/**
	 * @param loginMethod
	 *            the loginMethod to set
	 */
	public void setLoginMethod(String loginMethod) {
		this.loginMethod = loginMethod;
	}

	/**
	 * @return the langCode
	 */
	public String getLangCode() {
		return langCode;
	}

	/**
	 * @param langCode
	 *            the langCode to set
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

}
