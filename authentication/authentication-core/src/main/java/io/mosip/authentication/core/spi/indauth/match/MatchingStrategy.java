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
	/**
	 * gets the MatchFunction
	 * 
	 * @return MatchFunction
	 */
	public MatchFunction getMatchFunction();
	/**
	 * this method matches the request Values with entity values
	 * @param reqValues
	 * @param entityValues
	 * @param matchProperties
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	public default int match(Map<String, String> reqValues, Map<String, String> entityValues,
			Map<String, Object> matchProperties) throws IdAuthenticationBusinessException {
		return getMatchFunction().match(reqValues, entityValues, matchProperties);
	}
	
}
