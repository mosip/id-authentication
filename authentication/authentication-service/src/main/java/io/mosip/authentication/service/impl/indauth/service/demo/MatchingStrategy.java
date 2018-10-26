package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.function.ToIntBiFunction;

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
	 * Gets the match function.
	 *
	 * @return the match function
	 */
	 ToIntBiFunction<Object, Object> getMatchFunction();
	
}
