package io.mosip.authentication.common.service.impl.match;

import io.mosip.authentication.common.service.impl.AuthTypeImpl;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import org.springframework.util.CollectionUtils;

import java.util.Set;

public class KeyBindedTokenAuthTypeImpl extends AuthTypeImpl {

    /**
     * Instantiates a new auth type impl.
     *
     * @param type                 the type
     * @param associatedMatchTypes the associated match types
     */
    public KeyBindedTokenAuthTypeImpl(String type, Set<MatchType> associatedMatchTypes) {
        super(type, associatedMatchTypes, type);
    }

    @Override
    public boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher idInfoFetcher) {
        return  authReq instanceof KycAuthRequestDTO &&
                !CollectionUtils.isEmpty(((KycAuthRequestDTO)authReq).getRequest().getKeyBindedTokens());
    }
}
