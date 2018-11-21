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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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

	public static LocalDateTime parseUTCToLocalDateTime(String utcDateTime, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			return simpleDateFormat.parse(utcDateTime).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		} catch (ParseException e) {
			throw new io.mosip.kernel.core.exception.ParseException(
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getErrorCode(),
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getEexceptionMessage(), e);
		}

	}

	public static LocalDateTime parseUTCToLocalDateTime(Date date) {
		return date.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
	}

	public static Date parseUTCToDate(String utcDateTime, String pattern) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			return simpleDateFormat.parse(utcDateTime);
		} catch (ParseException e) {
			throw new io.mosip.kernel.core.exception.ParseException(
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getErrorCode(),
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getEexceptionMessage(), e);
		}
	}

	public static Date parseToDate(String dateTime, String pattern, TimeZone timeZone) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(timeZone);
		try {
			return simpleDateFormat.parse(dateTime);
		} catch (ParseException e) {
			throw new io.mosip.kernel.core.exception.ParseException(
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getErrorCode(),
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getEexceptionMessage(), e);
		}
	}

	public static String getUTCCurrentDateTimeString(String pattern) {
		return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern(pattern));
	}

	public static LocalDateTime getUTCCurrentDateTime() {
		return ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime();
	}

	// Default utcDateTime pattern - "yyyy-MM-dd'T'HH:mm:ss.SSS"
	public static String getDefaultUTCCurrentDateTimeString() {
		return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
	}

	// Default utcDateTime pattern - "yyyy-MM-dd'T'HH:mm:ss.SSS"
	public static LocalDateTime parseDefaultUTCToLocalDateTime(String utcDateTime) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			return simpleDateFormat.parse(utcDateTime).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		} catch (ParseException e) {
			throw new io.mosip.kernel.core.exception.ParseException(
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getErrorCode(),
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getEexceptionMessage(), e);
		}

	}

	// Default utcDateTime pattern - "yyyy-MM-dd'T'HH:mm:ss.SSS"
	public static Date parseDefaultUTCToDate(String utcDateTime) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			return simpleDateFormat.parse(utcDateTime);
		} catch (ParseException e) {
			throw new io.mosip.kernel.core.exception.ParseException(
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getErrorCode(),
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getEexceptionMessage(), e);
		}

	}

}
