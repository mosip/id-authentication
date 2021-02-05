package io.mosip.authentication.core.function;

/**
 * The Supplier FunctionalInterface with Throwable.
 *
 * @param <R> the generic type that is returned
 * @param <E> the element type that can be any Throwable
 * 
 * @author Loganathan Sekar
 */
@FunctionalInterface
public interface SupplierWithThrowable<R, E extends Throwable> {

	/**
	 * Get expression.
	 *
	 * @return the return value
	 * @throws E the exception
	 */
	R get() throws E;

}
