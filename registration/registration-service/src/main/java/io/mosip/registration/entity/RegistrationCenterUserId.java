package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Composite key for RegistrationCenterUser entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Embeddable
public class RegistrationCenterUserId implements Serializable {

	private static final long serialVersionUID = -7306845601917592413L;

	@Column(name = "regcntr_id")
	private String regcntrId;
	@Column(name = "usr_id")
	private String usrId;

	/**
	 * @return the regcntrId
	 */
	public String getRegcntrId() {
		return regcntrId;
	}

	/**
	 * @param regcntrId
	 *            the regcntrId to set
	 */
	public void setRegcntrId(String regcntrId) {
		this.regcntrId = regcntrId;
	}

	/**
	 * @return the usrId
	 */
	public String getUsrId() {
		return usrId;
	}

	/**
	 * @param usrId
	 *            the usrId to set
	 */
	public void setUsrId(String usrId) {
		this.usrId = usrId;
	}

}
