package io.mosip.authentication.core.indauth.dto;


import lombok.Data;

import java.util.List;

@Data
public class KycRequestDTO extends RequestDTO {

    /** H/W or S/W token  */
    private List<KeyBindedTokenDTO> keyBindedTokens;

    private String password;
}
