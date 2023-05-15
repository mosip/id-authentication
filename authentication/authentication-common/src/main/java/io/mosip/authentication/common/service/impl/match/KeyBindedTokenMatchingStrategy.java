package io.mosip.authentication.common.service.impl.match;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.*;

import java.util.Map;

public enum KeyBindedTokenMatchingStrategy implements MatchingStrategy {

    EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
        if (reqInfo instanceof Map && entityInfo instanceof Map) {
            Object object = props.get(IdaIdMapping.KEY_BINDED_TOKENS.getIdname());
            if (object instanceof TriFunctionWithBusinessException) {
                TriFunctionWithBusinessException<Map<String, String>,
                        Map<String, String>,
                        Map<String, Object>,
                        Double> func = (TriFunctionWithBusinessException<Map<String, String>,
                        Map<String, String>,
                        Map<String, Object>,
                        Double>) object;
                return (int) func.apply((Map<String, String>) reqInfo, (Map<String, String>) entityInfo, props)
                        .doubleValue();
            } else {
                throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.KEY_BINDING_CHECK_FAILED.getErrorCode(),
                        IdAuthenticationErrorConstants.KEY_BINDING_CHECK_FAILED.getErrorMessage());
            }
        }
        return 0;
    });

    private final MatchFunction matchFunction;

    /** The match strategy type. */
    private final MatchingStrategyType matchStrategyType;

    /**
     * Instantiates a new Token matching strategy.
     *
     * @param matchStrategyType the match strategy type
     * @param matchFunction the match function
     */
    private KeyBindedTokenMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
        this.matchFunction = matchFunction;
        this.matchStrategyType = matchStrategyType;
    }

    /* (non-Javadoc)
     * @see io.mosip.authentication.core.spi.indauth.match.MatchingStrategy#getType()
     */
    @Override
    public MatchingStrategyType getType() {
        return matchStrategyType;
    }

    /* (non-Javadoc)
     * @see io.mosip.authentication.core.spi.indauth.match.MatchingStrategy#getMatchFunction()
     */
    @Override
    public MatchFunction getMatchFunction() {
        return matchFunction;
    }


}
