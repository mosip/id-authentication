package io.mosip.authentication.core.indauth.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This Class Holds the values for Token Related data
 *
 * @author Anusha SE
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyBindedTokenDTO {

    private String type;
    private String token;
    private String format;
}
