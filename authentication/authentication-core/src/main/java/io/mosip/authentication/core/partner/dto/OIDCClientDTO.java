package io.mosip.authentication.core.partner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OIDC Client DTO
 * 
 * @author Mahammed Taheer
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OIDCClientDTO {

    private String[] authContextRefs;

    private String[] userClaims;
    
}
