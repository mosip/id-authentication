package io.mosip.authentication.common.service.impl.match;

import io.mosip.authentication.common.service.impl.AuthTypeImpl;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum TokenAuthType implements AuthType {

    TOKEN(IdaIdMapping.TOKEN.getIdname(), AuthType.setOf(TokenMatchType.TOKEN));

    private AuthTypeImpl authTypeImpl;

    /**
     * Instantiates a new demo auth type.
     *
     * @param type                 the type
     * @param associatedMatchTypes the associated match types
     */
    private TokenAuthType(String type, Set<MatchType> associatedMatchTypes) {
        authTypeImpl = new TokenAuthTypeImpl(type, associatedMatchTypes);
    }


    @Override
    public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO) {
        if(authRequestDTO instanceof KycAuthRequestDTO) {
            KycAuthRequestDTO kycAuthRequestDTO = (KycAuthRequestDTO)authRequestDTO;
            return kycAuthRequestDTO.getRequest().getTokenInfo() != null &&
                    kycAuthRequestDTO.getRequest().getTokenInfo().getToken() != null &&
                    kycAuthRequestDTO.getRequest().getTokenInfo().getTokenFormat() != null &&
                    kycAuthRequestDTO.getRequest().getTokenInfo().getTokenType() != null;
        }
        return false;
    }

    @Override
    public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
                                                  String language) {
        Map<String, Object> valueMap = new HashMap<>();
        if(isAuthTypeInfoAvailable(authRequestDTO)) {
            valueMap.put(IdaIdMapping.TOKEN.getIdname(), idInfoFetcher.getMatchFunction(this));
            valueMap.put(TokenAuthType.class.getSimpleName(), this);
        }
        return valueMap;
    }

    @Override
    public AuthType getAuthTypeImpl() {
        return authTypeImpl;
    }
}
