package io.mosip.kernel.masterdata.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
public final class ExceptionUtils {

	private ExceptionUtils() {
		super();
	}

	public static String parseException(Throwable ex) {
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
