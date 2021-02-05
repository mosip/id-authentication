package io.mosip.authentication.core.function;

/**
 * The Function Functional Throwable which can throw a Throwable.
 *
 * @param <R> the generic type of return value
 * @param <T> the generic type of argument
 * @param <E> the element type which can be a Throwable
 * 
 * @author Loganathan Sekar
 * 
 */
@FunctionalInterface
public interface FunctionWithThrowable<R, T, E extends Throwable> {

	/**
	 * Expression that accepts an argument and returns a value.
	 *
	 * @param t the argument
	 * @return the return value
	 * @throws E the exception
	 */
	R apply(T t) throws E;

}
