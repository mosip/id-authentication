package io.mosip.kernel.masterdata.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to get the Exception related functionalities.
 * 
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
	public static String getRootCauseMessage(Throwable ex) {
		String detailedMessage = null;

		final List<Throwable> list = new ArrayList<>();

		while (!EmptyCheckUtils.isNullEmpty(ex) && !list.contains(ex)) {
			list.add(ex);
			ex = ex.getCause();
		}

		Throwable rootCause = list.isEmpty() ? null : list.get(list.size() - 1);

		rootCause = rootCause == null ? ex : rootCause;

		detailedMessage = rootCause.getMessage();

		return detailedMessage;
	}

}
