package io.mosip.authentication.service.policy;

import java.util.List;

import lombok.Data;

/**
 * This class consolidates the authentication type attributes and KYC Attributes
 */
@Data
public class Policy {

	/** The list of auth type attributes. */
	private List<AuthPolicy> listAuthPolicy;
	
	/** The list of KYC attributes. */
	private List<KYCAttributes> listKYCAttributes;
}
