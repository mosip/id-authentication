package io.mosip.authentication.core.spi.authtype.acramr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticationFactor {

    /** Authentication Factor type */
    private String type;

    /** Authentication Factor count */
    private int count;
    
    /** Authentication Factor subtype */
    private List<String> subTypes;
}
