package io.mosip.registration.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * RegistrationAppLoginMethod entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "app_authentication_method")
public class RegistrationAppAuthenticationMethod extends RegistrationCommonFields {

	@EmbeddedId
	private RegistrationAppAuthenticationMethodId registrationAppAuthenticationMethodId;

	@Column(name = "method_seq")
	private int methodSeq;

	/**
	 * @return the registrationAppAuthenticationMethodId
	 */
	public RegistrationAppAuthenticationMethodId getregistrationAppAuthenticationMethodId() {
		return registrationAppAuthenticationMethodId;
	}

	/**
	 * @param registrationAppAuthenticationMethodId
	 *            the registrationAppAuthenticationMethodId to set
	 */
	public void setregistrationAppAuthenticationMethodId(RegistrationAppAuthenticationMethodId registrationAppAuthenticationMethodId) {
		this.registrationAppAuthenticationMethodId = registrationAppAuthenticationMethodId;
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

}
