package io.mosip.registration.processor.packet.service.dto.demographic;

import io.mosip.registration.processor.packet.service.dto.BaseDTO;

/**
 * This class used to capture the Demographic details of the Individual
 * 
 * @author Sowmya
 * @since 1.0.0
 */
public class DemographicInfoDTO extends BaseDTO {

	/** The identity. */
	private Identity identity;

	/**
	 * Gets the identity.
	 *
	 * @return the identity
	 */
	public Identity getIdentity() {
		return identity;
	}

	/**
	 * Sets the identity.
	 *
	 * @param identity
	 *            the identity to set
	 */
	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

}
