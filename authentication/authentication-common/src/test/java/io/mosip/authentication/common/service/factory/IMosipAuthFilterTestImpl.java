package io.mosip.authentication.common.service.factory;

import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.authfilter.spi.IMosipAuthFilter;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;

import java.util.List;
import java.util.Map;

public class IMosipAuthFilterTestImpl implements IMosipAuthFilter {
    @Override
    public void init() throws IdAuthenticationFilterException {
    }

    @Override
    public void validate(AuthRequestDTO authRequest, Map<String, List<IdentityInfoDTO>> identityData, Map<String, Object> properties) throws IdAuthenticationFilterException {
    }
}
