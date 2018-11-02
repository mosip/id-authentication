package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The Class RegCenterMachinePKEntity.
 */
@Embeddable
public class RegCenterMachinePKEntity implements Serializable {

	/**
	 * The Constant serialVersionUID.
	 *
	 * @author Girish Yarru
	 */
	private static final long serialVersionUID = 8916394292563999805L;
	
	/** The reg id. */
	@Column(name = "reg_id", nullable = false)
	private String regId;

	/**
	 * Gets the reg id.
	 *
	 * @return the reg id
	 */
	public String getRegId() {
		return regId;
	}

	/**
	 * Sets the reg id.
	 *
	 * @param regId the new reg id
	 */
	public void setRegId(String regId) {
		this.regId = regId;
	}
}
