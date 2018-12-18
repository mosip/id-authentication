package io.mosip.authentication.core.spi.indauth.match;

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
	
	/**`
	 * Gets the match function.
	 *
	 * @return the match function
	 */
	 MatchFunction getMatchFunction();
	
}
