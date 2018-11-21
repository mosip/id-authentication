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

	/**
	 * Default UTC TimeZone.
	 */
	private static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");
	/**
	 * Default UTC ZoneId.
	 */
	private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");
	/**
	 * Default UTC pattern.
	 */
	private static final String DEFAULT_UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

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

	// ---------------------------------------------------------------------------------------------
	/**
	 * Tests if this java.time.LocalDateTime is after the specified
	 * java.time.LocalDateTime.
	 * 
	 * @param d1
	 *            a java.time.LocalDateTime
	 * @param d2
	 *            a java.time.LocalDateTime
	 * @return <code>true</code> if and only if the instant represented by d1
	 *         <tt>LocalDateTime</tt> object is strictly later than the instant
	 *         represented by <tt>d2</tt>; <code>false</code> otherwise.
	 * @throws io.mosip.kernel.core.exception.IllegalArgumentException
	 *             if the <code>d1</code> , <code>d2</code> is null
	 */
	public static boolean after(LocalDateTime d1, LocalDateTime d2) {
		try {
			return d1.isAfter(d2);
		} catch (Exception e) {
			throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Tests if this LocalDateTime is before the specified LocalDateTime.
	 * 
	 * @param d1
	 *            a java.time.LocalDateTime
	 * @param d2
	 *            a java.time.LocalDateTime
	 * @return <code>true</code> if and only if the instant of time represented by
	 *         d1 <tt>LocalDateTime</tt> object is strictly earlier than the instant
	 *         represented by <tt>d2</tt>; <code>false</code> otherwise.
	 * @throws io.mosip.kernel.core.exception.IllegalArgumentException
	 *             if the <code>d1</code> , <code>d2</code> is null
	 * 
	 */
	public static boolean before(LocalDateTime d1, LocalDateTime d2) {
		try {
			return d1.isBefore(d2);
		} catch (Exception e) {
			throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Checks if two java.time.LocalDateTime objects are on the same day ignoring
	 * time. <br>
	 * 28 Mar 2002 13:45 and 28 Mar 2002 06:01 would return true.<br>
	 * 28 Mar 2002 13:45 and 12 Mar 2002 13:45 would return false.
	 * 
	 * @param d1
	 *            a java.time.LocalDateTime
	 * @param d2
	 *            a java.time.LocalDateTime
	 * @return <code>true</code> if they represent the same day
	 * @throws io.mosip.kernel.core.exception.IllegalArgumentException
	 *             if the <code>d1</code> , <code>d2</code> is null
	 * 
	 */
	public static boolean isSameDay(LocalDateTime d1, LocalDateTime d2) {
		try {
			return d1.toLocalDate().isEqual(d2.toLocalDate());
		} catch (Exception e) {
			throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Checks if two java.time.LocalDateTime objects represent the same instant in
	 * time. <br>
	 * This method compares the long millisecond time of the two objects.
	 * 
	 * @param d1
	 *            a java.time.LocalDateTime
	 * @param d2
	 *            a java.time.LocalDateTime
	 * @return <code>true</code> if they represent the same millisecond instant
	 * 
	 * @throws io.mosip.kernel.core.exception.IllegalArgumentException
	 *             if the <code>d1</code> , <code>d2</code> is null
	 */
	public static boolean isSameInstant(LocalDateTime d1, LocalDateTime d2) {
		try {
			return d1.isEqual(d2);
		} catch (Exception e) {
			throw new IllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}
	// ---------------------------------------------------------------------------------------------------------------------------

	/**
	 * This method returns LocalDateTime object for given utcDateTime string and
	 * pattern string.
	 * 
	 * @param utcDateTime
	 *            is of type java.lang.String
	 * @param pattern
	 *            is of type java.lang.String
	 * @return java.time.LocalDateTime
	 * @throws io.mosip.kernel.core.exception.ParseException
	 *             if can not able to parse the utcDateTime string for the pattern.
	 */
	public static LocalDateTime parseUTCToLocalDateTime(String utcDateTime, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(DateUtils.UTC_TIME_ZONE);
		try {
			return simpleDateFormat.parse(utcDateTime).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		} catch (ParseException e) {
			throw new io.mosip.kernel.core.exception.ParseException(
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getErrorCode(),
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getEexceptionMessage(), e);
		}

	}

	/**
	 * This method returns LocalDateTime without UTC for given Date object with UTC.
	 * 
	 * @param date
	 *            if of type java.util.Date
	 * @return java.time.LocalDateTime
	 */
	public static LocalDateTime parseUTCToLocalDateTime(Date date) {
		return date.toInstant().atZone(DateUtils.UTC_ZONE_ID).toLocalDateTime();
	}

	/**
	 * This method parse UTC dateTime string to java.lang.Date for given string
	 * pattern.
	 * 
	 * @param utcDateTime
	 *            is of type java.lang.String
	 * @param pattern
	 *            is of type java.lang.String
	 * @return java.util.Date
	 * @throws io.mosip.kernel.core.exception.ParseExceptioneException
	 *             if can not able to parse the dateTime string in given string
	 *             pattern.
	 */
	public static Date parseUTCToDate(String utcDateTime, String pattern) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(DateUtils.UTC_TIME_ZONE);
		try {
			return simpleDateFormat.parse(utcDateTime);
		} catch (ParseException e) {
			throw new io.mosip.kernel.core.exception.ParseException(
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getErrorCode(),
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getEexceptionMessage(), e);
		}
	}

	/**
	 * This method return java.lang.Date for given dateTime string, pattern and
	 * timeZone.
	 * 
	 * @param dateTime
	 *            is of type java.lang.String
	 * @param pattern
	 *            is of type java.lang.String
	 * @param timeZone
	 *            is of type java.util.TimeZone
	 * @return java.util.Date
	 * @throws io.mosip.kernel.core.exception.ParseExceptioneException
	 *             if can not able to parse the dateTime string in given string
	 *             pattern.
	 */
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

	/**
	 * This method return a date string of current UTC date for given pattern
	 * provided by the user.
	 * 
	 * @param pattern
	 *            is of type java.lang.String
	 * @return java.lang.String
	 */
	public static String getUTCCurrentDateTimeString(String pattern) {
		return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern(pattern));
	}

	/**
	 * This method return a current UTC java.time.LocalDateTime.
	 * 
	 * @return java.time.LocalDateTime
	 */
	public static LocalDateTime getUTCCurrentDateTime() {
		return ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime();
	}

	/**
	 * This method return a date string of current UTC date for given Default
	 * utcDateTime pattern - <b>yyyy-MM-dd'T'HH:mm:ss.SSS</b>.
	 * 
	 * @return java.lang.String
	 */
	public static String getDefaultUTCCurrentDateTimeString() {
		return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern(DateUtils.DEFAULT_UTC_PATTERN));
	}

	/**
	 * This method return a current UTC java.time.LocalDateTime for
	 * <code>utcDateTime</code> String with given Default utcDateTime pattern -
	 * <b>yyyy-MM-dd'T'HH:mm:ss.SSS</b>.
	 * 
	 * @param utcDateTime
	 * @return java.time.LocalDateTime
	 * @throws io.mosip.kernel.core.exception.ParseExceptioneException
	 *             if can not able to parse the <code>utcDateTime</code> string in
	 *             given Default utcDateTime pattern -
	 *             <b>yyyy-MM-dd'T'HH:mm:ss.SSS</b>.
	 */
	public static LocalDateTime parseDefaultUTCToLocalDateTime(String utcDateTime) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtils.DEFAULT_UTC_PATTERN);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			return simpleDateFormat.parse(utcDateTime).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		} catch (ParseException e) {
			throw new io.mosip.kernel.core.exception.ParseException(
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getErrorCode(),
					DateUtilConstants.PARSE_EXCEPTION_ERROR_CODE.getEexceptionMessage(), e);
		}

	}

	/**
	 * This method parse given <code>utcDateTime</code> String to java.util.Date
	 * with Default utcDateTime pattern - <b>yyyy-MM-dd'T'HH:mm:ss.SSS</b>.
	 * 
	 * @param utcDateTime
	 * @return java.util.Date
	 * @throws io.mosip.kernel.core.exception.ParseExceptioneException
	 *             if can not able to parse the <code>utcDateTime</code> string in
	 *             given Default utcDateTime pattern -
	 *             <b>yyyy-MM-dd'T'HH:mm:ss.SSS</b>.
	 */
	public static Date parseDefaultUTCToDate(String utcDateTime) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtils.DEFAULT_UTC_PATTERN);
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
