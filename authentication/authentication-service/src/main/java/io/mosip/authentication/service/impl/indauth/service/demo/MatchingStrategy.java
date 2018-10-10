package io.mosip.authentication.service.impl.indauth.service.demo;

import io.mosip.authentication.service.impl.indauth.service.demo.MatchStrategyType;

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
	MatchStrategyType getType();
	
	/**
	 * Gets the default match value.
	 *
	 * @return the default match value
	 */
	int getDefaultMatchValue();
	
	/**
	 * Gets the match function.
	 *
	 * @return the match function
	 */
	MatchFunction getMatchFunction();
	
}
