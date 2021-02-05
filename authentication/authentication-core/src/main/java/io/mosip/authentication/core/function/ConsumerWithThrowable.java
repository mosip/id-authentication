package io.mosip.authentication.core.function;

/**
 * Consumer Functional interface which can throw Exception.
 *
 * @param <T> the generic type
 * @param <E> the element type that can be any Throwable
 * 
 * @author Loganathan Sekar
 * 
 */
@FunctionalInterface
public interface ConsumerWithThrowable<T, E extends Throwable> {

	/**
	 * Expression that accepts the given arguments.
	 *
	 * @param t the first function argument
	 * @throws E the exception
	 */
	void accept(T t) throws E;

}