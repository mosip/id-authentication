package io.mosip.kernel.core.test.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import io.mosip.kernel.core.exception.ArithmeticException;
import io.mosip.kernel.core.exception.IllegalArgumentException;
import io.mosip.kernel.core.exception.NullPointerException;
import io.mosip.kernel.core.util.CalendarUtils;

public class CalendarUtilTest {

	private static DateFormat dateTimeParser = null;

	@Before
	public void setUp() throws Exception {
		dateTimeParser = new SimpleDateFormat("MMM dd, yyyy H:mm:ss.SSS", Locale.ENGLISH);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getCeilingTestCheckException()
			throws ParseException, IllegalArgumentException, ArithmeticException {
		CalendarUtils.getCeiling(null, Calendar.HOUR);
	}

	@Test(expected = ArithmeticException.class)
	public void getCeilingTestCheckException2()
			throws ParseException, ArithmeticException, IllegalArgumentException {

		Date d = dateTimeParser.parse("March 28, 280000001 13:52:10.099");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		CalendarUtils.getCeiling(c, Calendar.YEAR);
	}

	@Test(expected = ArithmeticException.class)
	public void getRoundCheckException2()
			throws ParseException, ArithmeticException, IllegalArgumentException {

		Date d = dateTimeParser.parse("March 28, 280000001 13:52:10.099");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		CalendarUtils.getRound(c, Calendar.YEAR);
	}

	@Test(expected = ArithmeticException.class)
	public void truncteCheckException() throws ParseException, ArithmeticException, IllegalArgumentException {

		Date d = dateTimeParser.parse("March 28, 280000001 13:52:10.099");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		CalendarUtils.truncate(c, Calendar.YEAR);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getFragmentInDaysTestCheckException()
			throws ParseException, IllegalArgumentException, ArithmeticException {
		CalendarUtils.getFragmentInDays(null, Calendar.HOUR);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getFragmentInHoursTestCheckException()
			throws ParseException, IllegalArgumentException, ArithmeticException {
		CalendarUtils.getFragmentInHours(null, Calendar.HOUR);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getFragmentInMilliSecondsTestCheckException()
			throws ParseException, IllegalArgumentException, ArithmeticException {
		CalendarUtils.getFragmentInMilliseconds(null, Calendar.HOUR);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getFragmentInMinutesTestCheckException()
			throws ParseException, IllegalArgumentException, ArithmeticException {
		CalendarUtils.getFragmentInMinutes(null, Calendar.HOUR);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getFragmentInSecondsTestCheckException()
			throws ParseException, IllegalArgumentException, ArithmeticException {
		CalendarUtils.getFragmentInSeconds(null, Calendar.HOUR);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isSameDayTestCheckException()
			throws ParseException, IllegalArgumentException, ArithmeticException {
		CalendarUtils.isSameDay(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isSameInstanceTestCheckException()
			throws ParseException, IllegalArgumentException, ArithmeticException {
		CalendarUtils.isSameInstant(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isSameLocalTimeTestCheckException()
			throws ParseException, IllegalArgumentException, ArithmeticException {
		CalendarUtils.isSameLocalTime(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getRoundTestCheckException()
			throws ParseException, IllegalArgumentException, ArithmeticException {
		CalendarUtils.getRound(null, Calendar.MONTH);
	}

	@Test(expected = NullPointerException.class)
	public void toCalendarTestCheckException() throws NullPointerException {
		CalendarUtils.toCalendar(null);
	}

	@Test(expected = NullPointerException.class)
	public void toCalendar2TestCheckException() throws NullPointerException {
		CalendarUtils.toCalendar(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void truncateTestCheckException()
			throws NullPointerException, IllegalArgumentException, ArithmeticException {
		CalendarUtils.truncate(null, Calendar.MONTH);
	}

	@Test(expected = IllegalArgumentException.class)
	public void truncatedEqualsTestCheckException()
			throws NullPointerException, IllegalArgumentException, ArithmeticException {
		CalendarUtils.truncatedEquals(null, null, Calendar.MONTH);
	}

	@Test
	public void getCeilingTest() throws ParseException, IllegalArgumentException, ArithmeticException {
		Date d = dateTimeParser.parse("March 28, 2002 13:52:10.099");
		Date d2 = dateTimeParser.parse("March 28, 2002 14:00:00.000");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d2);
		assertThat(CalendarUtils.getCeiling(c, Calendar.HOUR), is(c1));
	}

	@Test()
	public void getFragmentInDaysTest() throws ParseException, IllegalArgumentException {
		Date d = dateTimeParser.parse("January 28, 2008 13:52:10.099");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		assertThat(CalendarUtils.getFragmentInDays(c, Calendar.MONTH), is(28L));
	}

	@Test
	public void getFragmentInHoursTest() throws ParseException, IllegalArgumentException {
		Date d = dateTimeParser.parse("January 1, 2008 07:15:10.538");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		assertThat(CalendarUtils.getFragmentInHours(c, Calendar.MONTH), is(7L));
	}

	@Test
	public void getFragmentInMillisecondsTest() throws ParseException, IllegalArgumentException {
		Date d = dateTimeParser.parse("January 1, 2008 07:15:10.538");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		assertThat(CalendarUtils.getFragmentInMilliseconds(c, Calendar.SECOND), is(538L));
	}

	@Test
	public void getFragmentInMinutesTest() throws ParseException, IllegalArgumentException {
		Date d = dateTimeParser.parse("January 1, 2008 07:15:10.538");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		assertThat(CalendarUtils.getFragmentInMinutes(c, Calendar.HOUR_OF_DAY), is(15L));
	}

	@Test
	public void getFragmentInSecondsTest() throws ParseException, IllegalArgumentException {
		Date d = dateTimeParser.parse("January 1, 2008 07:15:10.538");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		assertThat(CalendarUtils.getFragmentInSeconds(c, Calendar.MINUTE), is(10L));
	}

	@Test
	public void isSameDayTest() throws ParseException, IllegalArgumentException {
		Date d = dateTimeParser.parse("January 1, 2008 07:15:10.538");
		Calendar c = Calendar.getInstance();
		Date d2 = dateTimeParser.parse("January 1, 2008 09:15:10.538");
		Calendar c2 = Calendar.getInstance();
		c.setTime(d);
		c2.setTime(d2);
		assertThat(CalendarUtils.isSameDay(c, c2), is(true));
	}

	@Test
	public void isSameInstantTest() throws ParseException, IllegalArgumentException {
		Date d = dateTimeParser.parse("January 1, 2008 07:15:10.751");
		Calendar c = Calendar.getInstance();
		Date d2 = dateTimeParser.parse("January 1, 2008 07:15:10.751");
		Calendar c2 = Calendar.getInstance();
		c.setTime(d);
		c2.setTime(d2);
		assertThat(CalendarUtils.isSameInstant(c, c2), is(true));
	}

	@Test
	public void isSameLocalTimeTest() throws ParseException, IllegalArgumentException {
		Date d = dateTimeParser.parse("January 1, 2008 07:15:10.751");
		Calendar c = Calendar.getInstance();
		Date d2 = dateTimeParser.parse("January 1, 2008 07:15:10.751");
		Calendar c2 = Calendar.getInstance();
		c.setTime(d);
		c2.setTime(d2);
		assertThat(CalendarUtils.isSameLocalTime(c, c2), is(true));
	}

	@Test
	public void getRoundTest() throws ParseException, IllegalArgumentException, ArithmeticException {
		Date d = dateTimeParser.parse("March 28, 2002 13:45:01.231");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		Date d2 = dateTimeParser.parse("March 28, 2002 14:00:00.000");
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		assertThat(CalendarUtils.getRound(c, Calendar.HOUR), is(c2));
	}

	@Test
	public void toCalendarTest() throws ParseException, NullPointerException {
		Date d = dateTimeParser.parse("March 28, 2002 13:45:01.231");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		assertThat(CalendarUtils.toCalendar(d), is(c));
	}

	@Test
	public void toCalendarWithTimeZoneTest() throws ParseException, NullPointerException {
		Date d = dateTimeParser.parse("March 28, 2002 13:45:01.231");
		TimeZone timeZone = TimeZone.getDefault();
		Calendar c = Calendar.getInstance(timeZone);
		c.setTime(d);
		assertThat(CalendarUtils.toCalendar(d, timeZone), is(c));
	}

	@Test
	public void truncateTest() throws ParseException, IllegalArgumentException, ArithmeticException {
		Date d = dateTimeParser.parse("March 28, 2002 13:45:01.231");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		Date d2 = dateTimeParser.parse("March 28, 2002 13:00:00.000");
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		assertThat(CalendarUtils.truncate(c, Calendar.HOUR), is(c2));
	}

	@Test
	public void truncatedEqualsTest() throws ParseException, IllegalArgumentException {
		Date d = dateTimeParser.parse("March 28, 2002 13:45:01.231");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		Date d2 = dateTimeParser.parse("March 28, 2002 13:00:00.000");
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		assertThat(CalendarUtils.truncatedEquals(c, c2, Calendar.HOUR), is(true));
	}
}
