package io.mosip.kernel.masterdata.utils;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 
 * @author Urvil Joshi
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
public final class ExceptionUtils {

	private ExceptionUtils() {
		super();
	}

	public static String parseException(Throwable ex) {
		Optional<Throwable> cause = Stream.iterate(ex, Throwable::getCause)
				.filter(element -> element.getCause() == null).findFirst();
		return cause.isPresent() ? cause.get().getMessage() : ex.getMessage();
	}

}


