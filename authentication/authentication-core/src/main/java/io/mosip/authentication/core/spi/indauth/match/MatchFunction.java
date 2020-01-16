package io.mosip.authentication.core.spi.indauth.match;

import java.util.Map;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * The Interface MatchFunction.
 *
 * @author Dinesh Karuppiah
 */

@FunctionalInterface
public interface MatchFunction {

	/**
	 * Match Function.
	 *
	 * @param reqValues the req values
	 * @param entityValues the entity values
	 * @param matchProperties the match properties
	 * @return the int
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */

	int match(Object reqValues, Object entityValues, Map<String, Object> matchProperties)
			throws IdAuthenticationBusinessException;

}
