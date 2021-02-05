package io.mosip.authentication.core.retry;

import java.util.function.Supplier;

/**
 * The Class FailingMockOperationWithRetry used in testing retry with methods
 * annotated with @WithRetry annotation.
 *
 * @param <T> the generic type
 * 
 * @author Loganathan Sekar
 * 
 */
public class FailingMockOperationWithRetry<T extends Exception> extends FailingMockOperation<T> {

	/**
	 * Instantiates a new failing mock operation with retry.
	 *
	 * @param failTimes         the fail times
	 * @param exceptionSupplier the exception supplier
	 */
	public FailingMockOperationWithRetry(int failTimes, Supplier<T> exceptionSupplier) {
		super(failTimes, exceptionSupplier);
	}

	/**
	 * Instantiates a new failing mock operation with retry.
	 *
	 * @param exceptionSupplier the exception supplier
	 */
	public FailingMockOperationWithRetry(Supplier<T> exceptionSupplier) {
		super(exceptionSupplier);
	}

	/**
	 * Gets the.
	 *
	 * @return the object
	 * @throws T the t
	 */
	@WithRetry
	@Override
	public Object get() throws T {
		return super.get();
	}

	/**
	 * Run.
	 *
	 * @throws T the t
	 */
	@WithRetry
	@Override
	public void run() throws T {
		super.run();
	}

	/**
	 * Accept.
	 *
	 * @param obj the obj
	 * @throws T the t
	 */
	@WithRetry
	@Override
	public void accept(Object obj) throws T {
		super.accept(obj);
	}

	/**
	 * Apply.
	 *
	 * @param obj the obj
	 * @return the object
	 * @throws T the t
	 */
	@WithRetry
	@Override
	public Object apply(Object obj) throws T {
		return super.apply(obj);
	}

}