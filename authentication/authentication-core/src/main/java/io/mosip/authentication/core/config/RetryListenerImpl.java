package io.mosip.authentication.core.config;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;

@Component
public class RetryListenerImpl implements RetryListener {
	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(RetryListenerImpl.class);

	@Override
	public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
		return true;
	}

	@Override
	public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
			Throwable throwable) {
	}

	/**
	 * On error.
	 *
	 * @param <T> the generic type
	 * @param <E> the element type
	 * @param context the context
	 * @param callback the callback
	 * @param throwable the throwable
	 */
	@Override
	public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
			Throwable throwable) {
		mosipLogger.error("", this.getClass().getSimpleName(), "onError",
				throwable.getMessage() + " : " + String.valueOf(context), throwable);
	}

}
