package io.mosip.authentication.core.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.function.ConsumerWithException;
import io.mosip.authentication.core.function.FunctionWithException;
import io.mosip.authentication.core.function.RunnableWithException;
import io.mosip.authentication.core.function.SupplierWithException;

/**
 * The RetryUtil - an Utility to invoke any APIs with retries as per the
 * configuration.
 */
@Component
public class RetryUtil {
	
	/** The retry template. */
	@Autowired
	private RetryTemplate retryTemplate;
	
	/**
	 * Do with retry.
	 *
	 * @param <R> the generic type
	 * @param <T> the generic type
	 * @param <E> the element type
	 * @param func the func
	 * @param t the t
	 * @return the r
	 * @throws E the e
	 */
	public <R, T, E extends Exception> R doWithRetry(FunctionWithException<R, T, E> func, T t) throws E {
		return doProcessWithRetry(func, t);
	}
	
	/**
	 * Do with retry.
	 *
	 * @param <R> the generic type
	 * @param <E> the element type
	 * @param func the func
	 * @return the r
	 * @throws E the e
	 */
	public <R, E extends Exception> R doWithRetry(SupplierWithException<R, E> func) throws E {
		return doProcessWithRetry(t -> func.get(), null);
	}
	
	/**
	 * Do with retry.
	 *
	 * @param <T> the generic type
	 * @param <E> the element type
	 * @param func the func
	 * @param t the t
	 * @throws E the e
	 */
	public <T, E extends Exception> void doWithRetry(ConsumerWithException<T, E> func,T t) throws E {
		this.<Void, T, E>doProcessWithRetry(t1 -> {
			func.accept(t1);
			return null;
		},t );
	}
	
	/**
	 * Do with retry.
	 *
	 * @param <E> the element type
	 * @param func the func
	 * @throws E the e
	 */
	public <E extends Exception> void doWithRetry(RunnableWithException<E> func) throws E {
		this.<Void, Void, E>doProcessWithRetry(t -> {
			func.run();
			return null;
		}, null);
	}
	
	/**
	 * Do process with retry.
	 *
	 * @param <R> the generic type
	 * @param <T> the generic type
	 * @param <E> the element type
	 * @param func the func
	 * @param t the t
	 * @return the r
	 * @throws E the e
	 */
	private <R, T, E extends Exception> R doProcessWithRetry(FunctionWithException<R, T, E> func, T t) throws E {
		R result = retryTemplate.execute(context -> func.apply(t));
		return result;
	}

}
