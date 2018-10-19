package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class RegCenterMachinePKEntity implements Serializable {

	/**
	 * @author Girish Yarru
	 */
	private static final long serialVersionUID = 8916394292563999805L;
	
	/** The reg id. */
	@Column(name = "reg_id", nullable = false)
	private String regId;

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}
}
