package io.mosip.authentication.core.indauth.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class KycAuthRequestDTO extends AuthRequestDTO {
    private KycRequestDTO request;
}
