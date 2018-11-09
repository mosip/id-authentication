package io.mosip.kernel.core.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.kernel.core.util.DateUtils;

/**
 * Unit test for simple App.
 */
@RunWith(MockitoJUnitRunner.class)
public final class DateUtilTest {

	private static Date TEST_DATE;

	private static Calendar TEST_CALANDER;

	private static String TEST_CALANDER_STRING;

	@BeforeClass
	public static void setup() {
		final GregorianCalendar cal = new GregorianCalendar(2018, 6, 5, 4, 3, 2);
		cal.set(Calendar.MILLISECOND, 1);
		TEST_DATE = cal.getTime();

		cal.setTimeZone(TimeZone.getDefault());

		TEST_CALANDER = cal;

		StringBuilder builder = new StringBuilder();
		builder.append(cal.get(Calendar.YEAR));
		builder.append(cal.get(Calendar.MONTH) + 1);
		builder.append(cal.get(Calendar.DAY_OF_MONTH));
		builder.append(cal.get(Calendar.HOUR_OF_DAY));
		TEST_CALANDER_STRING = builder.toString();
	}

	@Test
	public void testAddDays() throws Exception {
		Date result = DateUtils.addDays(TEST_DATE, 0);

		assertNotSame(TEST_DATE, result);
		assertDate(TEST_DATE, 2018, 6, 5, 4, 3, 2, 1);
		assertDate(result, 2018, 6, 5, 4, 3, 2, 1);

		result = DateUtils.addDays(TEST_DATE, 1);
		assertNotSame(TEST_DATE, result);
		assertDate(TEST_DATE, 2018, 6, 5, 4, 3, 2, 1);
		assertDate(result, 2018, 6, 6, 4, 3, 2, 1);

		result = DateUtils.addDays(TEST_DATE, -1);
		assertNotSame(TEST_DATE, result);
		assertDate(TEST_DATE, 2018, 6, 5, 4, 3, 2, 1);
		assertDate(result, 2018, 6, 4, 4, 3, 2, 1);

	}

	@Test
	public void testAddHours() throws Exception {
		Date result = DateUtils.addHours(TEST_DATE, 0);

		assertNotSame(TEST_DATE, result);
		assertDate(TEST_DATE, 2018, 6, 5, 4, 3, 2, 1);
		assertDate(result, 2018, 6, 5, 4, 3, 2, 1);

		result = DateUtils.addHours(TEST_DATE, 1);
		assertNotSame(TEST_DATE, result);
		assertDate(TEST_DATE, 2018, 6, 5, 4, 3, 2, 1);
		assertDate(result, 2018, 6, 5, 5, 3, 2, 1);

		result = DateUtils.addHours(TEST_DATE, -1);
		assertNotSame(TEST_DATE, result);
		assertDate(TEST_DATE, 2018, 6, 5, 4, 3, 2, 1);
		assertDate(result, 2018, 6, 5, 3, 3, 2, 1);

	}

	@Test
	public void testAddMinutes() throws Exception {
		Date result = DateUtils.addMinutes(TEST_DATE, 0);

		assertNotSame(TEST_DATE, result);
		assertDate(TEST_DATE, 2018, 6, 5, 4, 3, 2, 1);
		assertDate(result, 2018, 6, 5, 4, 3, 2, 1);

		result = DateUtils.addMinutes(TEST_DATE, 1);
		assertNotSame(TEST_DATE, result);
		assertDate(TEST_DATE, 2018, 6, 5, 4, 3, 2, 1);

		assertDate(result, 2018, 6, 5, 4, 4, 2, 1);

		result = DateUtils.addMinutes(TEST_DATE, -1);
		assertNotSame(TEST_DATE, result);
		assertDate(TEST_DATE, 2018, 6, 5, 4, 3, 2, 1);
		assertDate(result, 2018, 6, 5, 4, 2, 2, 1);

	}

	@Test
	public void testAddSeconds() throws Exception {
		Date result = DateUtils.addSeconds(TEST_DATE, 0);
		assertNotSame(TEST_DATE, result);
		assertDate(TEST_DATE, 2018, 6, 5, 4, 3, 2, 1);
		assertDate(result, 2018, 6, 5, 4, 3, 2, 1);

		result = DateUtils.addSeconds(TEST_DATE, 1);
		assertNotSame(TEST_DATE, result);
		assertDate(TEST_DATE, 2018, 6, 5, 4, 3, 2, 1);
		assertDate(result, 2018, 6, 5, 4, 3, 3, 1);

		result = DateUtils.addSeconds(TEST_DATE, -1);
		assertNotSame(TEST_DATE, result);
		assertDate(TEST_DATE, 2018, 6, 5, 4, 3, 2, 1);
		assertDate(result, 2018, 6, 5, 4, 3, 1, 1);
	}

	@Test
	public void testFormatCalender() throws Exception {
		assertEquals(TEST_CALANDER_STRING, DateUtils.formatCalendar(TEST_CALANDER, "yyyyMdH"));
		assertEquals(TEST_CALANDER_STRING, DateUtils.formatCalendar(TEST_CALANDER, "yyyyMdH", Locale.US));
		assertEquals(TEST_CALANDER_STRING, DateUtils.formatCalendar(TEST_CALANDER, "yyyyMdH", TimeZone.getDefault()));
		assertEquals(TEST_CALANDER_STRING,
				DateUtils.formatCalendar(TEST_CALANDER, "yyyyMdH", TimeZone.getDefault(), Locale.US));
	}

	@Test
	public void testFormatDate() throws Exception {
		assertEquals(TEST_CALANDER_STRING, DateUtils.formatDate(TEST_CALANDER.getTime(), "yyyyMdH"));
		assertEquals(TEST_CALANDER_STRING,
				DateUtils.formatDate(TEST_CALANDER.getTime(), "yyyyMdH", TimeZone.getDefault()));
		assertEquals(TEST_CALANDER_STRING,
				DateUtils.formatDate(TEST_CALANDER.getTime(), "yyyyMdH", TimeZone.getDefault(), Locale.US));
	}

	private void assertDate(final Date date, final int year, final int month, final int day, final int hour,
			final int min, final int sec, final int mil) throws Exception {
		final GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		assertEquals(year, cal.get(Calendar.YEAR));
		assertEquals(month, cal.get(Calendar.MONTH));
		assertEquals(day, cal.get(Calendar.DAY_OF_MONTH));
		assertEquals(hour, cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(min, cal.get(Calendar.MINUTE));
		assertEquals(sec, cal.get(Calendar.SECOND));
		assertEquals(mil, cal.get(Calendar.MILLISECOND));
	}

}
