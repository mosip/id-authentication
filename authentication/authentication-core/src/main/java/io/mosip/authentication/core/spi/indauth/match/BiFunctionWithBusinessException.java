package io.mosip.authentication.core.spi.indauth.match;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * 
 * 
 * 
 * 
 * Functional interface to throw Business Exception
 * 
 * @author Dinesh Karuppiah.T
 */
@FunctionalInterface
public interface BiFunctionWithBusinessException<T, U, R> {

	/**
	 * Applies this function to the given arguments.
	 *
	 * @param t the first function argument
	 * @param u the second function argument
	 * @return the function result
	 */
	R apply(T t, U u) throws IdAuthenticationBusinessException;

}