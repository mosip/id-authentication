package io.mosip.registration.processor.core.packet.dto.abis;

import java.io.Serializable;

/**
 * The Class ReferenceIdDto.
 */
public class ReferenceIdDto implements Serializable {

	private static final long serialVersionUID = 1L;

	/** The reference id. */
	private String referenceId;

	/**
	 * Gets the reference id.
	 *
	 * @return the reference id
	 */
	public String getReferenceId() {
		return referenceId;
	}

	/**
	 * Sets the reference id.
	 *
	 * @param referenceId the new reference id
	 */
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

}
