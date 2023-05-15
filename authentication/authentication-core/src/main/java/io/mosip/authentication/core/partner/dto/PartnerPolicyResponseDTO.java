package io.mosip.authentication.core.partner.dto;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * PartnerPolicy
 * 
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

	private String certificateData;

	private LocalDateTime policyExpiresOn;
	
	private LocalDateTime apiKeyExpiresOn;
	
	private LocalDateTime mispExpiresOn;

	private String mispPolicyId;

	private MispPolicyDTO mispPolicy;

	private OIDCClientDTO oidcClientDto;

}
