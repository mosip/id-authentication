package org.mosip.registration.entity;

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
@Table(schema = "reg", name = "app_login_method")
public class RegistrationAppLoginMethod extends RegistrationCommonFields {
	
	@EmbeddedId
	private RegistrationAppLoginMethodID registrationAppLoginMethodID;

	@Column(name = "method_seq", nullable = true, updatable = false)
	private int methodSeq;

	public RegistrationAppLoginMethodID getRegistrationAppLoginMethodID() {
		return registrationAppLoginMethodID;
	}

	public void setRegistrationAppLoginMethodID(RegistrationAppLoginMethodID registrationAppLoginMethodID) {
		this.registrationAppLoginMethodID = registrationAppLoginMethodID;
	}

	public int getMethodSeq() {
		return methodSeq;
	}

	public void setMethodSeq(int methodSeq) {
		this.methodSeq = methodSeq;
	}	
}
