package io.mosip.authentication.core.spi.authtype.acramr;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthMethodsRefValues {

    private Map<String, List<AuthenticationFactor>> authMethodsRefValues;
    
}
