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
public class RegistrationAppAuthenticationMethodId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "app_id")
	private String appId;
	@Column(name = "auth_method_code")
	private String authMethodCode;
	@Column(name = "process_name")
	private String processName;
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
		return authMethodCode;
	}

	/**
	 * @param loginMethod
	 *            the loginMethod to set
	 */
	public void setLoginMethod(String loginMethod) {
		this.authMethodCode = loginMethod;
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

	/**
	 * @return the processName
	 */
	public String getProcessName() {
		return processName;
	}

	/**
	 * @param processName
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	

}
