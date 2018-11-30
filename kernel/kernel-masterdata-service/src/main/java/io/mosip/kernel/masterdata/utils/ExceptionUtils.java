package io.mosip.kernel.masterdata.utils;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 
 * @author Urvil Joshi
 * @author Bal Vikash Sharma
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @author Dharmesh Khandelwal
 * 
 * @since 1.0.0
 */
public final class ExceptionUtils {
	/**
	 * Constructor for ExceptionUtils class.
	 */
	private ExceptionUtils() {
		super();
	}

	/**
	 * Method to find the root cause of the exception.
	 * 
	 * @param exception
	 *            the exception.
	 * @return the root cause.
	 */
	public static String parseException(Throwable exception) {
		Optional<Throwable> rootCause = Stream.iterate(exception, Throwable::getCause)
				.filter(element -> element.getCause() == null).findFirst();
		return rootCause.isPresent() ? rootCause.get().getMessage() : exception.getMessage();
	}
}
