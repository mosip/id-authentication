package io.mosip.authentication.core.partner.dto;

import java.util.List;

import lombok.Data;

/**
 * This class consolidates the authentication type attributes and KYC Attributes mapped with Policies json
 * @author Arun Bose S
 */
@Data
public class Policies {

	/** The list of auth type attributes. */
	private List<AuthPolicy> authPolicies;
	
	/** The list of KYC attributes. */
	private List<KYCAttributes> allowedKycAttributes;
}

