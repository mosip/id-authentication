package io.mosip.authentication.core.function;

/**
 * The Runnable Functional Interface which can throw a Throwable.
 *
 * @param <E> the element type which can be a Throwable
 * 
 * @author Loganathan Sekar
 * 
 */
@FunctionalInterface
public interface RunnableWithThrowable<E extends Throwable> {

	/**
	 * Expression to run
	 *
	 * @throws E the exception
	 */
	void run() throws E;

}
