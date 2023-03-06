package io.mosip.authentication.common.service.impl.match;

import io.mosip.authentication.common.service.impl.AuthTypeImpl;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;

import java.util.Set;

public class TokenAuthTypeImpl  extends AuthTypeImpl {

    /**
     * Instantiates a new auth type impl.
     *
     * @param type                 the type
     * @param associatedMatchTypes the associated match types
     */
    public TokenAuthTypeImpl(String type, Set<MatchType> associatedMatchTypes) {
        super(type, associatedMatchTypes, type);
    }

    @Override
    public boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher idInfoFetcher) {
        return (((KycAuthRequestDTO)authReq).getRequest().getTokenInfo() != null &&
                ((KycAuthRequestDTO)authReq).getRequest().getTokenInfo().getToken() != null);
    }
}
