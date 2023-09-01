package io.mosip.authentication.core.partner.dto;

import lombok.Data;

/**
 * MISP Policy DTO
 * 
 * @author Mahammed Taheer
 *
 */

@Data
public class MispPolicyDTO {

    private boolean allowKycRequestDelegation;

    private boolean allowOTPRequestDelegation;

    private boolean allowKeyBindingDelegation;

    private boolean allowVciRequestDelegation;
}
