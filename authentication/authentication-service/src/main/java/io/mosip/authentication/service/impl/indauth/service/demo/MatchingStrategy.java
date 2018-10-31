package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.function.ToIntBiFunction;

import io.mosip.authentication.core.dto.indauth.IdentityValue;

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
	 ToIntBiFunction<Object, IdentityValue> getMatchFunction();
	
}
