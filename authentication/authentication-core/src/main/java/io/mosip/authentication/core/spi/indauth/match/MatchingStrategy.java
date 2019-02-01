package io.mosip.authentication.core.spi.indauth.match;

import java.util.Map;
import java.util.stream.Collectors;

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
	
	public default int match(Map<String, String> reqValues, Map<String, String> entityValues,
			Map<String, Object> matchProperties) throws IdAuthenticationBusinessException {
		return getMatchFunction().match(reqValues, entityValues, matchProperties);
	}
	
}
