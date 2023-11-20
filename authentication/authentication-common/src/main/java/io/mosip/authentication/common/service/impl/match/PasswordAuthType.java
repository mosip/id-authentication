package io.mosip.authentication.common.service.impl.match;

import io.mosip.authentication.common.service.impl.AuthTypeImpl;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.ComparePasswordFunction;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public enum PasswordAuthType implements AuthType {

    PASSWORD(IdaIdMapping.PASSWORD.getIdname(), AuthType.setOf(PasswordMatchType.PASSWORD), "PASSWORD");

    private AuthTypeImpl authTypeImpl;

    /**
     * Instantiates a new demo auth type.
     *
     * @param type                 the type
     * @param associatedMatchTypes the associated match types
     */
    private PasswordAuthType(String type, Set<MatchType> associatedMatchTypes, String displayName) {
		authTypeImpl = new AuthTypeImpl(type, associatedMatchTypes, displayName);
    }


    @Override
    public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO) {
        if(authRequestDTO instanceof KycAuthRequestDTO) {
            KycAuthRequestDTO kycAuthRequestDTO = (KycAuthRequestDTO) authRequestDTO;
            return Objects.nonNull(kycAuthRequestDTO.getRequest().getPassword());
        }
        return false;
    }

    @Override
    public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
                                                  String language) {
        Map<String, Object> valueMap = new HashMap<>();
        if(isAuthTypeInfoAvailable(authRequestDTO)) {
            ComparePasswordFunction func = idInfoFetcher.getMatchPasswordFunction();
			valueMap.put(IdaIdMapping.PASSWORD.getIdname(), func);
        }
        return valueMap;
    }

    @Override
    public AuthType getAuthTypeImpl() {
        return authTypeImpl;
    }
}
