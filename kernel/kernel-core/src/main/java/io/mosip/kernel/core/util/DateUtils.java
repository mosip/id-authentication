/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mosip.kernel.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateFormatUtils;

import io.mosip.kernel.core.exception.IllegalArgumentException;
import io.mosip.kernel.core.util.constant.DateUtilConstants;

/**
 * Utilities for Date Time operations.
 * 
 * Provide Date and Time utility for usage across the application to manipulate
 * dates or calendars
 * 
 * @author Ravi C Balaji
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
public final class DateUtils {

	private static final Map<String, String> AVAILABLE_DATE_FORMATS = new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put("^\\d{8}$", "yyyyMMdd");
			put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
			put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
			put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
			put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
			put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
			put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
			put("^\\d{12}$", "yyyyMMddHHmm");
			put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
			put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
			put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
			put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
			put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
			put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
			put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
			put("^\\d{14}$", "yyyyMMddHHmmss");
			put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
			put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
			put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
			put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
			put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
			put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
			put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
		}
	};

	private DateUtils() {

	}

	/**
	 * <p>
	 * Adds a number of days to a date returning a new Date object.
	 * </p>
	 *
	 * @param date
	 *            the date, not null
	 * @param days
	 *            the number of days to add, may be negative
	 * @return the new Date with the number of days added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addDays(final Date date, final int days) {
		try {
			return org.apache.commons.lang3.time.DateUtils.addDays(date, days);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Adds a number of hours to a date returning a new Date object.
	 * </p>
	 *
	 * @param date
	 *            the date, not null
	 * @param hours
	 *            the hours to add, may be negative
	 * @return the new Date with the hours added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addHours(final Date date, final int hours) {
		try {
			return org.apache.commons.lang3.time.DateUtils.addHours(date, hours);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Adds a number of minutes to a date returning a new Date object.
	 * </p>
	 *
	 * @param date
	 *            the date, not null
	 * @param minutes
	 *            the minutes to add, may be negative
	 * @return the new Date with the minutes added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addMinutes(final Date date, final int minutes) {
		try {
			return org.apache.commons.lang3.time.DateUtils.addMinutes(date, minutes);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Adds a number of seconds to a date returning a new Date object.
	 * </p>
	 *
	 * @param date
	 *            the date, not null
	 * @param seconds
	 *            the seconds to add, may be negative
	 * @return the new Date with the seconds added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addSeconds(final Date date, final int seconds) {
		try {
			return org.apache.commons.lang3.time.DateUtils.addSeconds(date, seconds);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Formats a date/time into a specific pattern.
	 * </p>
	 *
	 * @param date
	 *            the date to format, not null
	 * @param pattern
	 *            the pattern to use to format the date, not null
	 * @return the formatted date
	 * @throws IllegalArgumentException
	 *             if the date/pattern is null
	 */
	public static String formatDate(final Date date, final String pattern) {
		try {
			return DateFormatUtils.format(date, pattern, null, null);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Formats a date/time into a specific pattern in a time zone.
	 * </p>
	 *
	 * @param date
	 *            the date to format, not null
	 * @param pattern
	 *            the pattern to use to format the date, not null
	 * @param timeZone
	 *            the time zone to use, may be null
	 * @return the formatted date
	 * @throws IllegalArgumentException
	 *             if the date/pattern/timeZone is null
	 */
	public static String formatDate(final Date date, final String pattern, final TimeZone timeZone) {
		try {
			return DateFormatUtils.format(date, pattern, timeZone, null);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Formats a date/time into a specific pattern in a time zone and locale.
	 * </p>
	 *
	 * @param date,
	 *            the date to format, not null
	 * @param pattern,
	 *            the pattern to use to format the date, not null
	 * @param timeZone,
	 *            the time zone to use, may be null
	 * @param locale,
	 *            the locale to use, may be null
	 * @return the formatted date
	 * @throws IllegalArgumentException
	 *             if the date/pattern/timeZone is null
	 */
	public static String formatDate(final Date date, final String pattern, final TimeZone timeZone,
			final Locale locale) {
		try {
			return DateFormatUtils.format(date, pattern, timeZone, locale);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	// -----------------------------------------------------------------------
	/**
	 * Formats a calendar into a specific pattern.
	 *
	 * @param calendar,
	 *            the calendar to format, not null
	 * @param pattern,
	 *            the pattern to use to format the calendar, not null
	 * @return the formatted calendar
	 * @throws IllegalArgumentException
	 *             if the calendar/pattern is null
	 */
	public static String formatCalendar(final Calendar calendar, final String pattern) {
		try {
			return DateFormatUtils.format(calendar, pattern, null, null);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	// -----------------------------------------------------------------------
	/**
	 * Formats a calendar into a specific pattern in a time zone.
	 *
	 * @param calendar,
	 *            the calendar to format, not null
	 * @param pattern,
	 *            the pattern to use to format the calendar, not null
	 * @param timeZone,
	 *            the time zone to use, may be null
	 * @return the formatted calendar
	 * @throws IllegalArgumentException
	 *             if the calendar/pattern/timeZone is null
	 */
	public static String formatCalendar(final Calendar calendar, final String pattern, final TimeZone timeZone) {
		try {
			return DateFormatUtils.format(calendar, pattern, timeZone, null);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	// -----------------------------------------------------------------------
	/**
	 * Formats a calendar into a specific pattern in a time zone and locale.
	 *
	 * @param calendar,
	 *            the calendar to format, not null
	 * @param pattern,
	 *            the pattern to use to format the calendar, not null
	 * @param locale,
	 *            the locale to use, may be null
	 * @return the formatted calendar
	 * @throws IllegalArgumentException
	 *             if the calendar/pattern/locale is null
	 */
	public static String formatCalendar(final Calendar calendar, final String pattern, final Locale locale) {
		try {
			return DateFormatUtils.format(calendar, pattern, null, locale);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	// -----------------------------------------------------------------------
	/**
	 * Formats a calendar into a specific pattern in a time zone and locale.
	 *
	 * @param calendar,
	 *            the calendar to format, not null
	 * @param pattern,
	 *            the pattern to use to format the calendar, not null
	 * @param timeZone,
	 *            the time zone to use, may be null
	 * @param locale,
	 *            the locale to use, may be null
	 * @return the formatted calendar
	 * @throws IllegalArgumentException
	 *             if the calendar/pattern/timeZone is null
	 */
	public static String formatCalendar(final Calendar calendar, final String pattern, final TimeZone timeZone,
			final Locale locale) {
		try {
			return DateFormatUtils.format(calendar, pattern, timeZone, locale);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------------
	/**
	 * Tests if this date is after the specified date.
	 * 
	 * @param d1
	 *            a java.util.Date
	 * @param d2
	 *            a java.util.Date
	 * @return <code>true</code> if and only if the instant represented by d1
	 *         <tt>Date</tt> object is strictly later than the instant represented
	 *         by <tt>d2</tt>; <code>false</code> otherwise.
	 * @throws io.mosip.kernel.core.exception.IllegalArgumentException
	 *             if the <code>d1</code> , <code>d2</code> is null
	 */
	public static boolean after(Date d1, Date d2) {
		try {
			return d1.after(d2);
		} catch (Exception e) {
			throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Tests if this date is before the specified date.
	 * 
	 * @param d1
	 *            a java.util.Date
	 * @param d2
	 *            a java.util.Date
	 * @return <code>true</code> if and only if the instant of time represented by
	 *         d1 <tt>Date</tt> object is strictly earlier than the instant
	 *         represented by <tt>d2</tt>; <code>false</code> otherwise.
	 * @throws io.mosip.kernel.core.exception.IllegalArgumentException
	 *             if the <code>d1</code> , <code>d2</code> is null
	 * 
	 */
	public static boolean before(Date d1, Date d2) {
		try {
			return d1.before(d2);
		} catch (Exception e) {
			throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Checks if two date objects are on the same day ignoring time. <br>
	 * 28 Mar 2002 13:45 and 28 Mar 2002 06:01 would return true.<br>
	 * 28 Mar 2002 13:45 and 12 Mar 2002 13:45 would return false.
	 * 
	 * @param d1
	 *            a java.util.Date
	 * @param d2
	 *            a java.util.Date
	 * @return <code>true</code> if they represent the same day
	 * @throws io.mosip.kernel.core.exception.IllegalArgumentException
	 *             if the <code>d1</code> , <code>d2</code> is null
	 * 
	 */
	public static boolean isSameDay(Date d1, Date d2) {
		try {
			return org.apache.commons.lang3.time.DateUtils.isSameDay(d1, d2);
		} catch (Exception e) {
			throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Checks if two date objects represent the same instant in time. <br>
	 * This method compares the long millisecond time of the two objects.
	 * 
	 * @param d1
	 *            a java.util.Date
	 * @param d2
	 *            a java.util.Date
	 * @return <code>true</code> if they represent the same millisecond instant
	 * 
	 * @throws io.mosip.kernel.core.exception.IllegalArgumentException
	 *             if the <code>d1</code> , <code>d2</code> is null
	 */
	public static boolean isSameInstant(Date d1, Date d2) {
		try {
			return org.apache.commons.lang3.time.DateUtils.isSameInstant(d1, d2);
		} catch (Exception e) {
			throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}
	// ---------------------------------------------------------------------------------------------------------------------------

	/**
	 * Parses a string representing a date by trying a variety of different parsers.
	 *
	 * @param str
	 *            the date to parse, not null
	 * @param parsePatterns
	 *            the date format patterns to use, see SimpleDateFormat, not null
	 * @return the parsed date
	 * @throws io.mosip.kernel.core.exception.IllegalArgumentException
	 *             if the date string or pattern array is null
	 * @throws io.mosip.kernel.core.exception.ParseException
	 *             if none of the date patterns were suitable (or there were none)
	 */
	public static Date parseDate(final String str, final String... parsePatterns) {
		try {
			return org.apache.commons.lang3.time.DateUtils.parseDate(str, parsePatterns);
		} catch (IllegalArgumentException | ParseException e) {
			e.printStackTrace();
			if (e instanceof IllegalArgumentException) {
				throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
						DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
			} else {
				throw new io.mosip.kernel.core.exception.ParseException(
						DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getErrorCode(),
						DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getEexceptionMessage(), e.getCause());
			}
		}
	}

	/**
	 * Parses a string representing a date by trying a variety of different parsers,
	 * using the default date format symbols for the given locale.
	 *
	 * @param str
	 *            the date to parse, not null
	 * @param locale
	 *            the locale whose date format symbols should be used. If
	 *            <code>null</code>, the system locale is used (as per
	 *            {@link #parseDate(String, String...)}).
	 * @param parsePatterns
	 *            the date format patterns to use, see SimpleDateFormat, not null
	 * @return the parsed date
	 * @throws io.mosip.kernel.core.exception.IllegalArgumentException
	 *             if the date string or pattern array is null
	 * @throws io.mosip.kernel.core.exception.ParseException
	 *             if none of the date patterns were suitable (or there were none)
	 */
	public static Date parseDate(final String str, final Locale locale, final String... parsePatterns) {
		try {
			return org.apache.commons.lang3.time.DateUtils.parseDate(str, locale, parsePatterns);
		} catch (IllegalArgumentException | ParseException e) {
			if (e instanceof IllegalArgumentException) {
				throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
						DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
			} else {
				throw new io.mosip.kernel.core.exception.ParseException(
						DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getErrorCode(),
						DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getEexceptionMessage(), e.getCause());
			}
		}
	}

	/**
	 * <p>
	 * Parses a string representing a date by trying a variety of different parsers.
	 * </p>
	 *
	 * <p>
	 * The parse will try each parse pattern in turn. A parse is only deemed
	 * successful if it parses the whole of the input string. If no parse patterns
	 * match, a ParseException is thrown.
	 * </p>
	 * The parser parses strictly - it does not allow for dates such as "February
	 * 942, 1996".
	 *
	 * @param str
	 *            the date to parse, not null
	 * @param parsePatterns
	 *            the date format patterns to use, see SimpleDateFormat, not null
	 * @return the parsed date
	 * @throws io.mosip.kernel.core.exception.IllegalArgumentException
	 *             if the date string or pattern array is null
	 * @throws io.mosip.kernel.core.exception.ParseException
	 *             if none of the date patterns were suitable
	 */
	public static Date parseDateStrictly(final String str, final String... parsePatterns) {
		try {
			return org.apache.commons.lang3.time.DateUtils.parseDateStrictly(str, null, parsePatterns);
		} catch (IllegalArgumentException | ParseException e) {
			if (e instanceof IllegalArgumentException) {
				throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
						DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
			} else {
				throw new io.mosip.kernel.core.exception.ParseException(
						DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getErrorCode(),
						DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getEexceptionMessage(), e.getCause());
			}
		}
	}

	/**
	 * <p>
	 * Parses a string representing a date by trying a variety of different parsers,
	 * using the default date format symbols for the given locale..
	 * </p>
	 *
	 * <p>
	 * The parse will try each parse pattern in turn. A parse is only deemed
	 * successful if it parses the whole of the input string. If no parse patterns
	 * match, a ParseException is thrown.
	 * </p>
	 * The parser parses strictly - it does not allow for dates such as "February
	 * 942, 1996".
	 *
	 * @param str
	 *            the date to parse, not null
	 * @param locale
	 *            the locale whose date format symbols should be used. If
	 *            <code>null</code>, the system locale is used (as per
	 *            {@link #parseDateStrictly(String, String...)}).
	 * @param parsePatterns
	 *            the date format patterns to use, see SimpleDateFormat, not null
	 * @return the parsed date
	 * @throws io.mosip.kernel.core.exception.IllegalArgumentException
	 *             if the date string or pattern array is null
	 * @throws io.mosip.kernel.core.exception.ParseException
	 *             if none of the date patterns were suitable
	 */
	public static Date parseDateStrictly(final String str, final Locale locale, final String... parsePatterns) {
		try {
			return org.apache.commons.lang3.time.DateUtils.parseDateStrictly(str, locale, parsePatterns);
		} catch (IllegalArgumentException | ParseException e) {
			if (e instanceof IllegalArgumentException) {
				throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
						DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
			} else {
				throw new io.mosip.kernel.core.exception.ParseException(
						DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getErrorCode(),
						DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getEexceptionMessage(), e.getCause());
			}
		}
	}

	/**
	 * Parse the given date string to date object and return a date instance based
	 * on the given date string. This makes use of the
	 * {@link DateUtils#determineDateFormat(String)} to determine the
	 * SimpleDateFormat pattern to be used for parsing.
	 * 
	 * @param dateString
	 *            The date string to be parsed to date object.
	 * @return The parsed date object.
	 * @throws io.mosip.kernel.core.exception.ParseException
	 *             If the date format pattern of the given date string is unknown,
	 *             or if the given date string or its actual date is invalid based
	 *             on the date format pattern.
	 * @throws io.mosip.kernel.core.exception.NullPointerException
	 *             If <code>dateString</code> is null.
	 */
	public static Date parse(String dateString) throws ParseException {
		if (Objects.isNull(dateString)) {
			throw new io.mosip.kernel.core.exception.NullPointerException(
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(),
					new NullPointerException("dateString is null"));
		}
		try {
			String dateFormat = determineDateFormat(dateString);
			if (dateFormat == null) {
				throw new io.mosip.kernel.core.exception.ParseException(
						DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getErrorCode(),
						DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getEexceptionMessage(),
						new ParseException("Unknown date format.", 0));
			}
			return parse(dateString, dateFormat);
		} catch (Exception e) {
			throw new io.mosip.kernel.core.exception.ParseException(
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getErrorCode(),
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Validate the actual date of the given date string based on the given date
	 * format pattern and return a date instance based on the given date string.
	 * 
	 * @param dateString
	 *            The date string.
	 * @param dateFormat
	 *            The date format pattern which should respect the SimpleDateFormat
	 *            rules.
	 * @return The parsed date object.
	 * @throws io.mosip.kernel.core.exception.ParseException
	 *             If the given date string or its actual date is invalid based on
	 *             the given date format pattern.
	 * @throws io.mosip.kernel.core.exception.NullPointerException
	 *             If <code>dateString</code> or <code>dateFormat</code> is null.
	 * @see SimpleDateFormat
	 */
	public static Date parse(String dateString, String dateFormat) throws ParseException {
		if (Objects.isNull(dateString) || Objects.isNull(dateFormat)) {
			throw new io.mosip.kernel.core.exception.NullPointerException(
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(),
					new NullPointerException("dateString or dateFormat is null"));
		}
		try {

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
			simpleDateFormat.setLenient(false); // Don't automatically convert invalid date.
			return simpleDateFormat.parse(dateString);

		} catch (Exception e) {
			throw new io.mosip.kernel.core.exception.ParseException(
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getErrorCode(),
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Determine SimpleDateFormat pattern matching with the given date string.
	 * Returns null if format is unknown.
	 * 
	 * @param dateString
	 *            The date string to determine the SimpleDateFormat pattern for.
	 * @return The matching SimpleDateFormat pattern, or null if format is unknown.
	 * @see SimpleDateFormat
	 */
	public static String determineDateFormat(String dateString) {
		for (String regexp : AVAILABLE_DATE_FORMATS.keySet()) {
			if (dateString.toLowerCase().matches(regexp)) {
				return AVAILABLE_DATE_FORMATS.get(regexp);
			}
		}
		return null; // Unknown format.
	}

}
