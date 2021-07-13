package io.mosip.authentication.core.partner.dto;

import java.util.List;
import java.util.Set;

import lombok.Data;

/**
 * PolicyDTO restricts the authorization type allowed,which is mapped with Auth Policies Json.
 * @author Arun Bose S
 */
@Data
public class PolicyDTO {
	
	/** The list of auth type attributes. */
	private List<AuthPolicy> allowedAuthTypes;
	
	/** The list of KYC attributes. */
	private List<KYCAttributes> allowedKycAttributes;
	
	private String authTokenType;
	
	/** Languages to sign kyc response. */
	private Set<String> kycLanguages;
	
}
