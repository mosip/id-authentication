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
package org.mosip.kernel.core.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.mosip.kernel.core.util.constant.DateUtilConstants;
import org.mosip.kernel.core.util.exception.MosipIllegalArgumentException;

/**
 * Utilities for Date Time operations.
 * 
 * Provide Date and Time utility for usage across the application to manipulate
 * dates or calendars
 * 
 * @author Ravi C Balaji
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
	 * @throws MosipIllegalArgumentException
	 *             if the date is null
	 */
	public static Date addDays(final Date date, final int days) {
		try {
			return org.apache.commons.lang3.time.DateUtils.addDays(date, days);
		} catch (IllegalArgumentException e) {
			throw new MosipIllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
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
	 * @throws MosipIllegalArgumentException
	 *             if the date is null
	 */
	public static Date addHours(final Date date, final int hours) {
		try {
			return org.apache.commons.lang3.time.DateUtils.addHours(date, hours);
		} catch (IllegalArgumentException e) {
			throw new MosipIllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
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
	 * @throws MosipIllegalArgumentException
	 *             if the date is null
	 */
	public static Date addMinutes(final Date date, final int minutes) {
		try {
			return org.apache.commons.lang3.time.DateUtils.addMinutes(date, minutes);
		} catch (IllegalArgumentException e) {
			throw new MosipIllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
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
	 * @throws MosipIllegalArgumentException
	 *             if the date is null
	 */
	public static Date addSeconds(final Date date, final int seconds) {
		try {
			return org.apache.commons.lang3.time.DateUtils.addSeconds(date, seconds);
		} catch (IllegalArgumentException e) {
			throw new MosipIllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
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
	 * @throws MosipIllegalArgumentException
	 *             if the date/pattern is null
	 */
	public static String formatDate(final Date date, final String pattern) {
		try {
			return DateFormatUtils.format(date, pattern, null, null);
		} catch (IllegalArgumentException e) {
			throw new MosipIllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
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
	 * @throws MosipIllegalArgumentException
	 *             if the date/pattern/timeZone is null
	 */
	public static String formatDate(final Date date, final String pattern, final TimeZone timeZone) {
		try {
			return DateFormatUtils.format(date, pattern, timeZone, null);
		} catch (IllegalArgumentException e) {
			throw new MosipIllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
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
	 * @throws MosipIllegalArgumentException
	 *             if the date/pattern/timeZone is null
	 */
	public static String formatDate(final Date date, final String pattern, final TimeZone timeZone,
			final Locale locale) {
		try {
			return DateFormatUtils.format(date, pattern, timeZone, locale);
		} catch (IllegalArgumentException e) {
			throw new MosipIllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
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
	 * @throws MosipIllegalArgumentException
	 *             if the calendar/pattern is null
	 */
	public static String formatCalendar(final Calendar calendar, final String pattern) {
		try {
			return DateFormatUtils.format(calendar, pattern, null, null);
		} catch (IllegalArgumentException e) {
			throw new MosipIllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
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
	 * @throws MosipIllegalArgumentException
	 *             if the calendar/pattern/timeZone is null
	 */
	public static String formatCalendar(final Calendar calendar, final String pattern, final TimeZone timeZone) {
		try {
			return DateFormatUtils.format(calendar, pattern, timeZone, null);
		} catch (IllegalArgumentException e) {
			throw new MosipIllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
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
	 * @throws MosipIllegalArgumentException
	 *             if the calendar/pattern/locale is null
	 */
	public static String formatCalendar(final Calendar calendar, final String pattern, final Locale locale) {
		try {
			return DateFormatUtils.format(calendar, pattern, null, locale);
		} catch (IllegalArgumentException e) {
			throw new MosipIllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
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
	 * @throws MosipIllegalArgumentException
	 *             if the calendar/pattern/timeZone is null
	 */
	public static String formatCalendar(final Calendar calendar, final String pattern, final TimeZone timeZone,
			final Locale locale) {
		try {
			return DateFormatUtils.format(calendar, pattern, timeZone, locale);
		} catch (IllegalArgumentException e) {
			throw new MosipIllegalArgumentException(DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					DateUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

}
