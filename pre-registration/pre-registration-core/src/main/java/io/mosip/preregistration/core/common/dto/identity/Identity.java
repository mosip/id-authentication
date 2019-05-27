package io.mosip.preregistration.core.common.dto.identity;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Instantiates a new identity.
 */
/**
 * @author Jagadishwari S
 * @since 1.0.0
 *
 */
@Data
@Component
public class Identity implements Serializable{

	/**
	 * constant serialVersion UID
	 */
	private static final long serialVersionUID = -608499873502236074L;

	/** The name. */
	private IdentityJsonValues name;

	/** The poa. */
	private IdentityJsonValues proofOfAddress;

	/** The postal code. */
	private IdentityJsonValues postalCode;

}
