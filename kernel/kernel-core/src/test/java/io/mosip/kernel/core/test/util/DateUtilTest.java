package io.mosip.kernel.core.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.kernel.core.exception.IllegalArgumentException;
import io.mosip.kernel.core.util.DateUtils;

/**
 * Unit test for simple App.
 */
@RunWith(MockitoJUnitRunner.class)
public final class DateUtilTest {

	private static Date TEST_DATE;

	private static Calendar TEST_CALANDER;

	private static String TEST_CALANDER_STRING;

	private static Date currDate;

	private static Calendar calendar;

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
		currDate = new Date();

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

	// --------------------------------- Test for after---------------
	private void loadDate() {
		calendar = Calendar.getInstance();
		calendar.setTime(currDate);
	}

	@Test
	public void testDateAfter() {

		loadDate();
		calendar.add(Calendar.DATE, 1);

		Date nextDate = calendar.getTime();

		assertTrue(DateUtils.after(nextDate, currDate));

		assertFalse(DateUtils.after(currDate, nextDate));

		assertFalse(DateUtils.after(currDate, currDate));
	}

	// --------------------------------- Test for before-------------------
	@Test
	public void testDateBefore() {

		loadDate();
		calendar.add(Calendar.DATE, -1);
		Date previousDay = calendar.getTime();

		assertTrue(DateUtils.before(previousDay, currDate));

		assertFalse(DateUtils.before(currDate, previousDay));

		assertFalse(DateUtils.before(currDate, currDate));
	}

	// --------------------------------- Test for equal----------------------
	@Test
	public void testIsSameDayWithNextDate() {
		loadDate();
		calendar.add(Calendar.DATE, 1);
		Date nextDate = calendar.getTime();

		assertTrue(DateUtils.isSameDay(currDate, currDate));

		assertFalse(DateUtils.isSameDay(currDate, nextDate));

		assertFalse(DateUtils.isSameDay(nextDate, currDate));
	}

	@Test
	public void testIsSameDayWithDifferentTime() {
		loadDate();
		calendar.add(Calendar.HOUR, 1);
		calendar.add(Calendar.MINUTE, 1);
		calendar.add(Calendar.SECOND, 1);
		calendar.add(Calendar.MILLISECOND, 1);
		Date nextDate = calendar.getTime();

		assertTrue(DateUtils.isSameDay(currDate, currDate));

		assertTrue(DateUtils.isSameDay(currDate, nextDate));

		assertTrue(DateUtils.isSameDay(nextDate, currDate));
	}

	@Test
	public void testIsSameInstantWithDifferentTime() {
		loadDate();
		calendar.add(Calendar.HOUR, 1);
		calendar.add(Calendar.MINUTE, 1);
		calendar.add(Calendar.SECOND, 1);
		calendar.add(Calendar.MILLISECOND, 1);
		Date nextDate = calendar.getTime();

		assertTrue(DateUtils.isSameInstant(currDate, currDate));

		assertFalse(DateUtils.isSameInstant(currDate, nextDate));

		assertFalse(DateUtils.isSameInstant(nextDate, currDate));
	}

	// --------------------------------- Test for exception----------------------
	@Test(expected = IllegalArgumentException.class)
	public void testDateAfterExceptionBothDateNull() {
		DateUtils.after(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDateBeforeExceptionBothDateNull() {
		DateUtils.before(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDateEqualExceptionBothDateNull() {
		DateUtils.isSameDay(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsSameInstantExceptionBothDateNull() {
		DateUtils.isSameInstant(null, null);
	}

	// -----------------------------Parsing date test----------------------------


}
