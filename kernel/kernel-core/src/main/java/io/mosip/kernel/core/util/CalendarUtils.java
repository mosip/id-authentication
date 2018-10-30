package io.mosip.kernel.core.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateUtils;

import io.mosip.kernel.core.exception.ArithmeticException;
import io.mosip.kernel.core.exception.IllegalArgumentException;
import io.mosip.kernel.core.exception.NullPointerException;
import io.mosip.kernel.core.util.constant.CalendarUtilConstants;

/**
 * This class provides the calendar utilities for MOSIP implementations.
 * 
 * @author Sagar Mahapatra
 * @since version 1.0.0
 */
public final class CalendarUtils {
	
	/**
	 * private constructor for CalendarUtil class.
	 */
	private CalendarUtils() {
		
	}

	/**
	 * Gets a date ceiling, leaving the field specified as the most significant
	 * field.
	 * 
	 * @param calendar
	 *            the date to work with, not null
	 * @param field
	 *            the field from
	 * @return the different ceil date, not null
	 * @throws IllegalArgumentException
	 *             if the date is null
	 * @throws ArithmeticException
	 *             if the year is over 280 million
	 */
	public static Calendar getCeiling(Calendar calendar, int field) {
		try {
			return DateUtils.ceiling(calendar, field);
		} catch (java.lang.IllegalArgumentException exception) {
			throw new IllegalArgumentException(CalendarUtilConstants.ILLEGAL_ARGUMENT_CODE.getErrorCode(),
					CalendarUtilConstants.ILLEGAL_ARGUMENT_MESSAGE.getErrorCode(), exception.getCause());
		} catch (java.lang.ArithmeticException exception) {
			throw new ArithmeticException(CalendarUtilConstants.ARITHMETIC_EXCEPTION_CODE.getErrorCode(),
					CalendarUtilConstants.YEAR_OVERFLOW_MESSAGE.getErrorCode(), exception.getCause());
		}
	}

	/**
	 * Returns the number of days within the fragment. All datefields greater than
	 * the fragment will be ignored.
	 * 
	 * @param calendar
	 *            the calendar to work with, not null
	 * @param fragment
	 *            the field part of calendar to calculate
	 * @return number of days within the fragment of date
	 * @throws IllegalArgumentException
	 *             if the date is null or fragment is not supported
	 */
	public static long getFragmentInDays(Calendar calendar, int fragment) {
		try {
			return DateUtils.getFragmentInDays(calendar, fragment);
		} catch (java.lang.IllegalArgumentException exception) {
			throw new IllegalArgumentException(CalendarUtilConstants.ILLEGAL_ARGUMENT_CODE.getErrorCode(),
					CalendarUtilConstants.ILLEGAL_ARGUMENT_MESSAGE.getErrorCode(), exception.getCause());
		}
	}

	/**
	 * Returns the number of hours within the fragment. All datefields greater than
	 * the fragment will be ignored.
	 * 
	 * @param calendar
	 *            the calendar to work with, not null
	 * @param fragment
	 *            the field part of calendar to calculate
	 * @return number of hours within the fragment of date
	 * @throws IllegalArgumentException
	 *             if the date is null or fragment is not supported
	 * @since 2.4
	 */

	public static long getFragmentInHours(Calendar calendar, int fragment) {
		try {
			return DateUtils.getFragmentInHours(calendar, fragment);
		} catch (java.lang.IllegalArgumentException exception) {
			throw new IllegalArgumentException(CalendarUtilConstants.ILLEGAL_ARGUMENT_CODE.getErrorCode(),
					CalendarUtilConstants.ILLEGAL_ARGUMENT_MESSAGE.getErrorCode(), exception.getCause());
		}
	}

	/**
	 * Returns the number of milliseconds within the fragment. All datefields
	 * greater than the fragment will be ignored.
	 * 
	 * @param calendar
	 *            the calendar to work with, not null
	 * @param fragment
	 *            the field part of calendar to calculate
	 * @return number of milliseconds within the fragment of date
	 * @throws IllegalArgumentException
	 *             if the date is null or fragment is not supported
	 */

	public static long getFragmentInMilliseconds(Calendar calendar, int fragment) {
		try {
			return DateUtils.getFragmentInMilliseconds(calendar, fragment);
		} catch (java.lang.IllegalArgumentException exception) {
			throw new IllegalArgumentException(CalendarUtilConstants.ILLEGAL_ARGUMENT_CODE.getErrorCode(),
					CalendarUtilConstants.ILLEGAL_ARGUMENT_MESSAGE.getErrorCode(), exception.getCause());
		}
	}

	/**
	 * Returns the number of minutes within the fragment. All datefields greater
	 * than the fragment will be ignored.
	 * 
	 * @param calendar
	 *            the calendar to work with, not null
	 * @param fragment
	 *            the field part of calendar to calculate
	 * @return number of minutes within the fragment of date
	 * @throws IllegalArgumentException
	 *             if the date is null or fragment is not supported
	 */
	public static long getFragmentInMinutes(Calendar calendar, int fragment) {
		try {
			return DateUtils.getFragmentInMinutes(calendar, fragment);
		} catch (java.lang.IllegalArgumentException exception) {
			throw new IllegalArgumentException(CalendarUtilConstants.ILLEGAL_ARGUMENT_CODE.getErrorCode(),
					CalendarUtilConstants.ILLEGAL_ARGUMENT_MESSAGE.getErrorCode(), exception.getCause());
		}
	}

	/**
	 * Returns the number of seconds within the fragment. All datefields greater
	 * than the fragment will be ignored.
	 * 
	 * @param calendar
	 *            the calendar to work with, not null
	 * @param fragment
	 *            the field part of calendar to calculate
	 * @return number of seconds within the fragment of date
	 * @throws IllegalArgumentException
	 *             if the date is null or fragment is not supported
	 */
	public static long getFragmentInSeconds(Calendar calendar, int fragment) {
		try {
			return DateUtils.getFragmentInSeconds(calendar, fragment);
		} catch (java.lang.IllegalArgumentException exception) {
			throw new IllegalArgumentException(CalendarUtilConstants.ILLEGAL_ARGUMENT_CODE.getErrorCode(),
					CalendarUtilConstants.ILLEGAL_ARGUMENT_MESSAGE.getErrorCode(), exception.getCause());
		}
	}

	/**
	 * Checks if two calendar objects are on the same day ignoring time.
	 * 
	 * @param cal1
	 *            the first calendar, not altered, not null
	 * @param cal2
	 *            the second calendar, not altered, not null
	 * @return true if they represent the same day
	 * @throws IllegalArgumentException
	 *             if either calendar is null
	 */
	public static boolean isSameDay(Calendar cal1, Calendar cal2) {
		try {
			return DateUtils.isSameDay(cal1, cal2);
		} catch (java.lang.IllegalArgumentException exception) {
			throw new IllegalArgumentException(CalendarUtilConstants.ILLEGAL_ARGUMENT_CODE.getErrorCode(),
					CalendarUtilConstants.ILLEGAL_ARGUMENT_MESSAGE.getErrorCode(), exception.getCause());
		}
	}

	/**
	 * Checks if two calendar objects represent the same instant in time. This
	 * method compares the long millisecond time of the two objects.
	 * 
	 * @param cal1
	 *            the first calendar, not altered, not null
	 * @param cal2
	 *            the second calendar, not altered, not null
	 * @return true if they represent the same millisecond instant
	 * @throws IllegalArgumentException
	 *             if either date is null
	 */
	public static boolean isSameInstant(Calendar cal1, Calendar cal2) {
		try {
			return DateUtils.isSameInstant(cal1, cal2);
		} catch (java.lang.IllegalArgumentException exception) {
			throw new IllegalArgumentException(CalendarUtilConstants.ILLEGAL_ARGUMENT_CODE.getErrorCode(),
					CalendarUtilConstants.ILLEGAL_ARGUMENT_MESSAGE.getErrorCode(), exception.getCause());
		}
	}

	/**
	 * Checks if two calendar objects represent the same local time. This method
	 * compares the values of the fields of the two objects. In addition, both
	 * calendars must be the same of the same type.
	 * 
	 * @param cal1
	 *            the first calendar, not altered, not null
	 * @param cal2
	 *            the second calendar, not altered, not null
	 * @return true if they represent the same millisecond instant
	 * @throws IllegalArgumentException
	 *             if either date is null
	 */
	public static boolean isSameLocalTime(Calendar cal1, Calendar cal2) {
		try {
			return DateUtils.isSameLocalTime(cal1, cal2);
		} catch (java.lang.IllegalArgumentException exception) {
			throw new IllegalArgumentException(CalendarUtilConstants.ILLEGAL_ARGUMENT_CODE.getErrorCode(),
					CalendarUtilConstants.ILLEGAL_ARGUMENT_MESSAGE.getErrorCode(), exception.getCause());
		}
	}

	/**
	 * Rounds a date, leaving the field specified as the most significant field.
	 * 
	 * @param calendar
	 *            the date to work with, not null
	 * @param field
	 *            the field from Calendar
	 * @return the different rounded date, not null
	 * @throws IllegalArgumentException
	 *             if the date is null
	 * @throws ArithmeticException
	 *             if the year is over 280 million
	 */
	public static Calendar getRound(Calendar calendar, int field) {
		try {
			return DateUtils.round(calendar, field);
		} catch (java.lang.IllegalArgumentException exception) {
			throw new IllegalArgumentException(CalendarUtilConstants.ILLEGAL_ARGUMENT_CODE.getErrorCode(),
					CalendarUtilConstants.ILLEGAL_ARGUMENT_MESSAGE.getErrorCode(), exception.getCause());
		} catch (java.lang.ArithmeticException exception) {
			throw new ArithmeticException(CalendarUtilConstants.ARITHMETIC_EXCEPTION_CODE.getErrorCode(),
					CalendarUtilConstants.YEAR_OVERFLOW_MESSAGE.getErrorCode(), exception.getCause());
		}
	}

	/**
	 * Converts a Date of a given TimeZone into a Calendar.
	 * 
	 * @param date
	 *            the date to convert to a Calendar
	 * @return the created Calendar
	 * @throws NullPointerException
	 *             if date is null
	 */
	public static Calendar toCalendar(Date date) {
		try {
			return DateUtils.toCalendar(date);
		} catch (java.lang.NullPointerException exception) {
			throw new NullPointerException(CalendarUtilConstants.NULL_ARGUMENT_CODE.getErrorCode(),
					CalendarUtilConstants.DATE_NULL_MESSAGE.getErrorCode(), exception.getCause());
		}
	}

	/**
	 * @param date
	 *            the date to convert to a Calendar
	 * @param tz
	 *            the time zone of the date
	 * @return the created calendar
	 * @throws NullPointerException
	 *             if date or timezone is null
	 */
	public static Calendar toCalendar(Date date, TimeZone tz) {
		try {
			return DateUtils.toCalendar(date, tz);
		} catch (java.lang.NullPointerException exception) {
			throw new NullPointerException(CalendarUtilConstants.NULL_ARGUMENT_CODE.getErrorCode(),
					CalendarUtilConstants.DATE_NULL_MESSAGE.getErrorCode(), exception.getCause());
		}
	}

	/**
	 * Truncates a date, leaving the field specified as the most significant field.
	 * 
	 * @param date
	 *            the date to work with, not null
	 * @param field
	 *            the field from Calendar
	 * @return the different truncated date, not null
	 * @throws IllegalArgumentException
	 *             if the date is null
	 * @throws ArithmeticException
	 *             if the year is over 280 million
	 */
	public static Calendar truncate(Calendar date, int field) {
		try {
			return DateUtils.truncate(date, field);
		} catch (java.lang.IllegalArgumentException exception) {
			throw new IllegalArgumentException(CalendarUtilConstants.ILLEGAL_ARGUMENT_CODE.getErrorCode(),
					CalendarUtilConstants.ILLEGAL_ARGUMENT_MESSAGE.getErrorCode(), exception.getCause());
		} catch (java.lang.ArithmeticException exception) {
			throw new ArithmeticException(CalendarUtilConstants.ARITHMETIC_EXCEPTION_CODE.getErrorCode(),
					CalendarUtilConstants.YEAR_OVERFLOW_MESSAGE.getErrorCode(), exception.getCause());
		}
	}

	/**
	 * Determines if two calendars are equal up to no more than the specified most
	 * significant field.
	 * 
	 * @param cal1
	 *            the first calendar, not null
	 * @param cal2
	 *            the second calendar, not null
	 * @param field
	 *            the field from Calendar
	 * @return true if equal; otherwise false
	 * @throws IllegalArgumentException
	 *             if any argument is null
	 */
	public static boolean truncatedEquals(Calendar cal1, Calendar cal2, int field) {
		try {
			return DateUtils.truncatedEquals(cal1, cal2, field);
		} catch (java.lang.IllegalArgumentException exception) {
			throw new IllegalArgumentException(CalendarUtilConstants.ILLEGAL_ARGUMENT_CODE.getErrorCode(),
					CalendarUtilConstants.ILLEGAL_ARGUMENT_MESSAGE.getErrorCode(), exception.getCause());
		}
	}
}
