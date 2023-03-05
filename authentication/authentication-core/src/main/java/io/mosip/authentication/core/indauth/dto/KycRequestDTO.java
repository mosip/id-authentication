package io.mosip.authentication.core.indauth.dto;


import lombok.Data;

@Data
public class KycRequestDTO extends RequestDTO {

    /** H/W or S/W token  */
    private TokenInfoDTO tokenInfo;
}
