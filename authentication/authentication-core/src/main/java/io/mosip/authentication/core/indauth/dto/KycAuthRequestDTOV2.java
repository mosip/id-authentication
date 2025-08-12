package io.mosip.authentication.core.indauth.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class KycAuthRequestDTOV2 extends AuthRequestDTO {
    
    private KycRequestDTOV2 request;

    private boolean claimsMetadataRequired;
}
