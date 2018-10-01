package org.mosip.kernel.core.util.test;

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
import org.mosip.kernel.core.util.CalendarUtils;
import org.mosip.kernel.core.util.exception.MosipArithmeticException;
import org.mosip.kernel.core.util.exception.MosipIllegalArgumentException;
import org.mosip.kernel.core.util.exception.MosipNullPointerException;

public class CalendarUtilTest {

	private static DateFormat dateTimeParser = null;

	@Before
	public void setUp() throws Exception {
		dateTimeParser = new SimpleDateFormat("MMM dd, yyyy H:mm:ss.SSS", Locale.ENGLISH);
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getCeilingTestCheckException()
			throws ParseException, MosipIllegalArgumentException, MosipArithmeticException {
		CalendarUtils.getCeiling(null, Calendar.HOUR);
	}

	@Test(expected = MosipArithmeticException.class)
	public void getCeilingTestCheckException2()
			throws ParseException, MosipArithmeticException, MosipIllegalArgumentException {

		Date d = dateTimeParser.parse("March 28, 280000001 13:52:10.099");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		CalendarUtils.getCeiling(c, Calendar.YEAR);
	}

	@Test(expected = MosipArithmeticException.class)
	public void getRoundCheckException2()
			throws ParseException, MosipArithmeticException, MosipIllegalArgumentException {

		Date d = dateTimeParser.parse("March 28, 280000001 13:52:10.099");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		CalendarUtils.getRound(c, Calendar.YEAR);
	}

	@Test(expected = MosipArithmeticException.class)
	public void truncteCheckException() throws ParseException, MosipArithmeticException, MosipIllegalArgumentException {

		Date d = dateTimeParser.parse("March 28, 280000001 13:52:10.099");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		CalendarUtils.truncate(c, Calendar.YEAR);
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getFragmentInDaysTestCheckException()
			throws ParseException, MosipIllegalArgumentException, MosipArithmeticException {
		CalendarUtils.getFragmentInDays(null, Calendar.HOUR);
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getFragmentInHoursTestCheckException()
			throws ParseException, MosipIllegalArgumentException, MosipArithmeticException {
		CalendarUtils.getFragmentInHours(null, Calendar.HOUR);
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getFragmentInMilliSecondsTestCheckException()
			throws ParseException, MosipIllegalArgumentException, MosipArithmeticException {
		CalendarUtils.getFragmentInMilliseconds(null, Calendar.HOUR);
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getFragmentInMinutesTestCheckException()
			throws ParseException, MosipIllegalArgumentException, MosipArithmeticException {
		CalendarUtils.getFragmentInMinutes(null, Calendar.HOUR);
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getFragmentInSecondsTestCheckException()
			throws ParseException, MosipIllegalArgumentException, MosipArithmeticException {
		CalendarUtils.getFragmentInSeconds(null, Calendar.HOUR);
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void isSameDayTestCheckException()
			throws ParseException, MosipIllegalArgumentException, MosipArithmeticException {
		CalendarUtils.isSameDay(null, null);
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void isSameInstanceTestCheckException()
			throws ParseException, MosipIllegalArgumentException, MosipArithmeticException {
		CalendarUtils.isSameInstant(null, null);
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void isSameLocalTimeTestCheckException()
			throws ParseException, MosipIllegalArgumentException, MosipArithmeticException {
		CalendarUtils.isSameLocalTime(null, null);
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getRoundTestCheckException()
			throws ParseException, MosipIllegalArgumentException, MosipArithmeticException {
		CalendarUtils.getRound(null, Calendar.MONTH);
	}

	@Test(expected = MosipNullPointerException.class)
	public void toCalendarTestCheckException() throws MosipNullPointerException {
		CalendarUtils.toCalendar(null);
	}

	@Test(expected = MosipNullPointerException.class)
	public void toCalendar2TestCheckException() throws MosipNullPointerException {
		CalendarUtils.toCalendar(null, null);
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void truncateTestCheckException()
			throws MosipNullPointerException, MosipIllegalArgumentException, MosipArithmeticException {
		CalendarUtils.truncate(null, Calendar.MONTH);
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void truncatedEqualsTestCheckException()
			throws MosipNullPointerException, MosipIllegalArgumentException, MosipArithmeticException {
		CalendarUtils.truncatedEquals(null, null, Calendar.MONTH);
	}

	@Test
	public void getCeilingTest() throws ParseException, MosipIllegalArgumentException, MosipArithmeticException {
		Date d = dateTimeParser.parse("March 28, 2002 13:52:10.099");
		Date d2 = dateTimeParser.parse("March 28, 2002 14:00:00.000");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d2);
		assertThat(CalendarUtils.getCeiling(c, Calendar.HOUR), is(c1));
	}

	@Test()
	public void getFragmentInDaysTest() throws ParseException, MosipIllegalArgumentException {
		Date d = dateTimeParser.parse("January 28, 2008 13:52:10.099");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		assertThat(CalendarUtils.getFragmentInDays(c, Calendar.MONTH), is(28L));
	}

	@Test
	public void getFragmentInHoursTest() throws ParseException, MosipIllegalArgumentException {
		Date d = dateTimeParser.parse("January 1, 2008 07:15:10.538");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		assertThat(CalendarUtils.getFragmentInHours(c, Calendar.MONTH), is(7L));
	}

	@Test
	public void getFragmentInMillisecondsTest() throws ParseException, MosipIllegalArgumentException {
		Date d = dateTimeParser.parse("January 1, 2008 07:15:10.538");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		assertThat(CalendarUtils.getFragmentInMilliseconds(c, Calendar.SECOND), is(538L));
	}

	@Test
	public void getFragmentInMinutesTest() throws ParseException, MosipIllegalArgumentException {
		Date d = dateTimeParser.parse("January 1, 2008 07:15:10.538");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		assertThat(CalendarUtils.getFragmentInMinutes(c, Calendar.HOUR_OF_DAY), is(15L));
	}

	@Test
	public void getFragmentInSecondsTest() throws ParseException, MosipIllegalArgumentException {
		Date d = dateTimeParser.parse("January 1, 2008 07:15:10.538");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		assertThat(CalendarUtils.getFragmentInSeconds(c, Calendar.MINUTE), is(10L));
	}

	@Test
	public void isSameDayTest() throws ParseException, MosipIllegalArgumentException {
		Date d = dateTimeParser.parse("January 1, 2008 07:15:10.538");
		Calendar c = Calendar.getInstance();
		Date d2 = dateTimeParser.parse("January 1, 2008 09:15:10.538");
		Calendar c2 = Calendar.getInstance();
		c.setTime(d);
		c2.setTime(d2);
		assertThat(CalendarUtils.isSameDay(c, c2), is(true));
	}

	@Test
	public void isSameInstantTest() throws ParseException, MosipIllegalArgumentException {
		Date d = dateTimeParser.parse("January 1, 2008 07:15:10.751");
		Calendar c = Calendar.getInstance();
		Date d2 = dateTimeParser.parse("January 1, 2008 07:15:10.751");
		Calendar c2 = Calendar.getInstance();
		c.setTime(d);
		c2.setTime(d2);
		assertThat(CalendarUtils.isSameInstant(c, c2), is(true));
	}

	@Test
	public void isSameLocalTimeTest() throws ParseException, MosipIllegalArgumentException {
		Date d = dateTimeParser.parse("January 1, 2008 07:15:10.751");
		Calendar c = Calendar.getInstance();
		Date d2 = dateTimeParser.parse("January 1, 2008 07:15:10.751");
		Calendar c2 = Calendar.getInstance();
		c.setTime(d);
		c2.setTime(d2);
		assertThat(CalendarUtils.isSameLocalTime(c, c2), is(true));
	}

	@Test
	public void getRoundTest() throws ParseException, MosipIllegalArgumentException, MosipArithmeticException {
		Date d = dateTimeParser.parse("March 28, 2002 13:45:01.231");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		Date d2 = dateTimeParser.parse("March 28, 2002 14:00:00.000");
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		assertThat(CalendarUtils.getRound(c, Calendar.HOUR), is(c2));
	}

	@Test
	public void toCalendarTest() throws ParseException, MosipNullPointerException {
		Date d = dateTimeParser.parse("March 28, 2002 13:45:01.231");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		assertThat(CalendarUtils.toCalendar(d), is(c));
	}

	@Test
	public void toCalendarWithTimeZoneTest() throws ParseException, MosipNullPointerException {
		Date d = dateTimeParser.parse("March 28, 2002 13:45:01.231");
		TimeZone timeZone = TimeZone.getDefault();
		Calendar c = Calendar.getInstance(timeZone);
		c.setTime(d);
		assertThat(CalendarUtils.toCalendar(d, timeZone), is(c));
	}

	@Test
	public void truncateTest() throws ParseException, MosipIllegalArgumentException, MosipArithmeticException {
		Date d = dateTimeParser.parse("March 28, 2002 13:45:01.231");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		Date d2 = dateTimeParser.parse("March 28, 2002 13:00:00.000");
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		assertThat(CalendarUtils.truncate(c, Calendar.HOUR), is(c2));
	}

	@Test
	public void truncatedEqualsTest() throws ParseException, MosipIllegalArgumentException {
		Date d = dateTimeParser.parse("March 28, 2002 13:45:01.231");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		Date d2 = dateTimeParser.parse("March 28, 2002 13:00:00.000");
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		assertThat(CalendarUtils.truncatedEquals(c, c2, Calendar.HOUR), is(true));
	}
}
