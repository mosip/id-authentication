package io.mosip.registration.test.integrationtest;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.mosip.registration.dto.BaseDTO;
import io.mosip.registration.dto.demographic.Identity;

/**
 * This class used to capture the Demographic details of the Individual
 * 
 * @author Dinesh Asokan
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class DemographicInfoDTOMix extends BaseDTO {

	/** The identity. */
	@JsonIgnore
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
