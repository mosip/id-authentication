package io.mosip.authentication.common.service.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.util.DateUtils2;

/**
 * Stop-gap for kernel-core 1.4.1 {@link DateUtils2#parseToDate(String, String)}, which fails for
 * date-only patterns (e.g. {@code yyyy/MM/dd}) because it converts the parsed value with
 * {@code Instant.from(..)}. Date-time patterns are still handled by {@link DateUtils2}; when that
 * fails, the value is parsed as a date and taken at the start of the day in the system zone.
 * <p>
 * TODO: remove once DateUtils2 in kernel-core handles date-only patterns.
 */
public final class IdaDateUtil {

	private IdaDateUtil() {
	}

	/**
	 * Parses the date string with the given pattern, supporting date-only patterns.
	 *
	 * @param dateString the date string
	 * @param pattern    the date pattern
	 * @return the parsed date (start of day in system zone for date-only patterns)
	 * @throws ParseException if the value cannot be parsed
	 */
	public static Date parseToDate(String dateString, String pattern) {
		try {
			return DateUtils2.parseToDate(dateString, pattern);
		} catch (ParseException e) {
			try {
				ZoneId zone = ZoneId.systemDefault();
				LocalDate date = LocalDate.from(
						DateTimeFormatter.ofPattern(pattern, Locale.getDefault()).withZone(zone).parse(dateString));
				return Date.from(date.atStartOfDay(zone).toInstant());
			} catch (RuntimeException ignored) {
				throw e;
			}
		}
	}
}
