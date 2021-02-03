package io.mosip.authentication.core.function;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * Consumer Functional interface to throw Exception.
 *
 * @author Loganathan Sekaran
 * @param <T> the generic type
 */
@FunctionalInterface
public interface ConsumerWithException<T, E extends Exception> {

	/**
	 * Applies this function to the given arguments.
	 *
	 * @param t the first function argument
	 * @return the function result
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	void accept(T t) throws E;

}