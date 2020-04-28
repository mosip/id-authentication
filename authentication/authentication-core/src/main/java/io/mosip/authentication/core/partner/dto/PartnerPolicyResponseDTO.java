package io.mosip.authentication.core.partner.dto;
import lombok.Data;

/**
 *  PartnerPolicy
 * @author Nagarjuna
 *
 */
@Data
public class PartnerPolicyResponseDTO {

	private String policyId;
	
	private String policyName;
	
	private String policyDescription;
	
	private boolean policyStatus;
	
	private PolicyDTO policy;
	
	private String partnerId;
	
	private String partnerName;
}

