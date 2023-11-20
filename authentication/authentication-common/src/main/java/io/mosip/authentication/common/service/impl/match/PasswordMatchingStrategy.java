package io.mosip.authentication.common.service.impl.match;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SEMI_COLON;

import java.util.Map;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.ComparePasswordFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.util.DemoMatcherUtil;
import io.mosip.kernel.core.logger.spi.Logger;

public enum PasswordMatchingStrategy implements MatchingStrategy {

    EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
        if (reqInfo instanceof Map && entityInfo instanceof Map) {
			Object object = props.get(IdaIdMapping.PASSWORD.getIdname());
            if (object instanceof ComparePasswordFunction) {
                ComparePasswordFunction func = (ComparePasswordFunction) object;
                Map<String, String> entityInfoMap = (Map<String, String>) entityInfo;
                Map<String, String> reqInfoMap = (Map<String, String>) reqInfo;
                String[] hashSaltValue = entityInfoMap.get("password").split(SEMI_COLON);
                String passwordHashedValue = hashSaltValue[0];
                String salt = hashSaltValue[1];
                String reqInfoValue = reqInfoMap.get(IdaIdMapping.PASSWORD.getIdname());
				boolean matched = func.matchPasswordFunction(reqInfoValue, passwordHashedValue, salt);
                return !matched ? 0 : 100;
            } else {
                logError();
                throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PASSWORD_MISMATCH.getErrorCode(), 
                        IdAuthenticationErrorConstants.PASSWORD_MISMATCH.getErrorMessage());
            }
        }
        return 0;
    });

    private final MatchFunction matchFunction;

    /** The match strategy type. */
    private final MatchingStrategyType matchStrategyType;

    private static Logger mosipLogger = IdaLogger.getLogger(PasswordMatchingStrategy.class);

    /**
     * Instantiates a new Token matching strategy.
     *
     * @param matchStrategyType the match strategy type
     * @param matchFunction the match function
     */
    private PasswordMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
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

    private static void logError() {
        mosipLogger.error(IdAuthCommonConstants.SESSION_ID, IdAuthCommonConstants.PASSWORD_BASED_AUTH, 
                    "Error in Passward Matching Strategy");
    }

    public static DemoMatcherUtil getDemoMatcherUtilObject(Map<String, Object> props) {
		return (DemoMatcherUtil)props.get("demoMatcherUtil");
	}

}
