package io.mosip.authentication.common.service.helper;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.spi.indauth.match.EntityValueFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 @author Kamesh Shekhar Prasad
 */

@Component
public class MatchIdentityDataHelper {

    @Autowired
    MatchTypeHelper matchTypeHelper;
    /**
     * Match identity data.
     *
     * @param authRequestDTO     the auth request DTO
     * @param uin                the uin
     * @param listMatchInputs    the list match inputs
     * @param entityValueFetcher the entity value fetcher
     * @param partnerId the partner id
     * @return the list
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    public List<MatchOutput> matchIdentityData(AuthRequestDTO authRequestDTO, String uin,
                                                      Collection<MatchInput> listMatchInputs, EntityValueFetcher entityValueFetcher, String partnerId)
            throws IdAuthenticationBusinessException {
        List<MatchOutput> matchOutputList = new ArrayList<>();
        for (MatchInput matchInput : listMatchInputs) {
            MatchOutput matchOutput = matchTypeHelper.matchType(authRequestDTO, uin, matchInput, entityValueFetcher, partnerId);
            if (matchOutput != null) {
                matchOutputList.add(matchOutput);
            }
        }
        return matchOutputList;
    }


}
