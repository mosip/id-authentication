package io.mosip.kernel.masterdata.utils;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * This class is used to get the Exception related functionalities.
 * 
 * @author Urvil Joshi
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
public final class ExceptionUtils {

	private ExceptionUtils() {
		super();
	}

	/**
	 * This message is used to get the root cause message of the exception.
	 * 
	 * @param ex
	 *            is of type {@link Throwable}
	 * @return the root cause message.
	 */
	public static String parseException(Throwable ex) {
		Optional<Throwable> cause = Stream.iterate(ex, Throwable::getCause)
				.filter(element -> element.getCause() == null).findFirst();
		return cause.isPresent() ? cause.get().getMessage() : ex.getMessage();
	}

}


