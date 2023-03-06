package io.mosip.authentication.core.indauth.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This Class Holds the values for Token Related data
 *
 * @author Prem Kumar
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfoDTO {

    private String tokenType;
    private String token;
    private String tokenFormat;
}
