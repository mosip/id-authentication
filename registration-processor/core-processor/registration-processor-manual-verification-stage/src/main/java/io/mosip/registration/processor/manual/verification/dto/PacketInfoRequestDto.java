package io.mosip.registration.processor.manual.verification.dto;

import java.io.Serializable;

/**
 * The Class PacketInfoRequestDto.
 */
public class PacketInfoRequestDto implements Serializable {

	private static final long serialVersionUID = 5112224635778738335L;
	/** The reg id. */
	private String regId;

	/**
	 * Gets the reg id.
	 *
	 * @return the regId
	 */
	public String getRegId() {
		return regId;
	}

	/**
	 * Sets the reg id.
	 *
	 * @param regId the regId to set
	 */
	public void setRegId(String regId) {
		this.regId = regId;
	}
	
	

}
