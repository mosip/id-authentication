package io.mosip.authentication.core.spi.indauth.match;

import java.util.Map;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * @author  Arun Bose
 * The Interface MatchingStrategy.
 */
public interface MatchingStrategy {

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	MatchingStrategyType getType();
	
	public MatchFunction getMatchFunction();
	
	public default int match(Map<String, String> reqValues, Map<String, String> entityValues, Map<String, Object> matchProperties) throws IdAuthenticationBusinessException {
		String reqInfo = reqValues.values().stream().findFirst().orElse("");
		String entityInfo = entityValues.values().stream().findFirst().orElse("");
		return  getMatchFunction().match(reqInfo, entityInfo, matchProperties);
	}
	
}
