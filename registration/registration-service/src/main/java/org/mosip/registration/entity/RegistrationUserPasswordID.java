package org.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * Composite key for RegistrationUserPassword entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Embeddable
public class RegistrationUserPasswordID implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "usr_id", length = 64, nullable = false, updatable = false)
	private String usrId;
	@Column(name = "pwd", length = 512, nullable = false, updatable = false)
	private String pwd;

	public String getUsrId() {
		return usrId;
	}

	public void setUsrId(String usrId) {
		this.usrId = usrId;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

}
