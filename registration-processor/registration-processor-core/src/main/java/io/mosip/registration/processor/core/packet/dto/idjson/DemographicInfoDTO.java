package io.mosip.registration.processor.core.packet.dto.idjson;


/**
 * This class used to capture the Demographic details of the Individual
 * 
 * @author Dinesh Asokan
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class DemographicInfoDTO {

	/** The identity. */
	private IdentityJson identity;

	/**
	 * Gets the identity.
	 *
	 * @return the identity
	 */
	public IdentityJson getIdentity() {
		return identity;
	}

	/**
	 * Sets the identity.
	 *
	 * @param identity
	 *            the identity to set
	 */
	public void setIdentity(IdentityJson identity) {
		this.identity = identity;
	}

}
