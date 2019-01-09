package io.mosip.registration.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * AppAuthenticationMethod entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "app_authentication_method")
public class AppAuthenticationMethod extends RegistrationCommonFields {

	@EmbeddedId
	private AppAuthenticationMethodId appAuthenticationMethodId;

	@Column(name = "method_seq")
	private int methodSeq;
	
	@Column(name= "role_code")
	private String roleCode;

	/**
	 * @return the appAuthenticationMethodId
	 */
	public AppAuthenticationMethodId getAppAuthenticationMethodId() {
		return appAuthenticationMethodId;
	}

	/**
	 * @param appAuthenticationMethodId
	 *            the appAuthenticationMethodId to set
	 */
	public void setAppAuthenticationMethodId(AppAuthenticationMethodId appAuthenticationMethodId) {
		this.appAuthenticationMethodId = appAuthenticationMethodId;
	}

	/**
	 * @return the methodSeq
	 */
	public int getMethodSeq() {
		return methodSeq;
	}

	/**
	 * @param methodSeq
	 *            the methodSeq to set
	 */
	public void setMethodSeq(int methodSeq) {
		this.methodSeq = methodSeq;
	}
	
	/**
	 * @return the roleCode
	 */
	public String getRoleCode() {
		return roleCode;
	}

	/**
	 * @param roleCode the roleCode to set
	 */
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}


}
