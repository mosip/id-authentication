package io.mosip.authentication.common.service.impl.match;

import io.mosip.authentication.common.service.impl.AuthTypeImpl;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum KeyBindedTokenAuthType implements AuthType {

    KEYBINDEDTOKEN(IdaIdMapping.KEY_BINDED_TOKENS.getIdname(), AuthType.setOf(KeyBindedTokenMatchType.KEY_BINDED_TOKENS));

    private AuthTypeImpl authTypeImpl;

    /**
     * Instantiates a new demo auth type.
     *
     * @param type                 the type
     * @param associatedMatchTypes the associated match types
     */
    private KeyBindedTokenAuthType(String type, Set<MatchType> associatedMatchTypes) {
        authTypeImpl = new KeyBindedTokenAuthTypeImpl(type, associatedMatchTypes);
    }


    @Override
    public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO) {
        if(authRequestDTO instanceof KycAuthRequestDTO) {
            KycAuthRequestDTO kycAuthRequestDTO = (KycAuthRequestDTO)authRequestDTO;
            return !CollectionUtils.isEmpty(kycAuthRequestDTO.getRequest().getKeyBindedTokens()) &&
                    kycAuthRequestDTO.getRequest().getKeyBindedTokens().get(0).getToken() != null &&
                    kycAuthRequestDTO.getRequest().getKeyBindedTokens().get(0).getFormat() != null &&
                    kycAuthRequestDTO.getRequest().getKeyBindedTokens().get(0).getType() != null;
        }
        return false;
    }

    @Override
    public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
                                                  String language) {
        Map<String, Object> valueMap = new HashMap<>();
        if(isAuthTypeInfoAvailable(authRequestDTO)) {
            valueMap.put(IdaIdMapping.KEY_BINDED_TOKENS.getIdname(), idInfoFetcher.getMatchFunction(this));
            valueMap.put(KeyBindedTokenAuthType.class.getSimpleName(), this);
        }
        return valueMap;
    }

    @Override
    public AuthType getAuthTypeImpl() {
        return authTypeImpl;
    }
}
