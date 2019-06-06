package io.mosip.authentication.core.spi.indauth.match;

import java.util.Map;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 *
 * The Interface MatchingStrategy adopts the various matching strategies across all authtypes and its corresponding attributes.
 *  @author  Arun Bose
 */
public interface MatchingStrategy {

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public default MatchingStrategyType getType() {
		return getMatchingStrategy().getType();
	}
	
	/**
	 * gets the MatchFunction
	 * 
	 * @return MatchFunction
	 */
	public default MatchFunction getMatchFunction() {
		return getMatchingStrategy().getMatchFunction();
	}
	
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
	
	/**
	 * Gets the matching strategy.
	 *
	 * @return the matching strategy
	 */
	public default MatchingStrategy getMatchingStrategy() {
		return null;
	}
	
}
