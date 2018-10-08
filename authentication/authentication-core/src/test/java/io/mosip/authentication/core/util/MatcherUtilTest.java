package io.mosip.authentication.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class MatcherUtilTest {

	@Test
	public void TestValiddoExactMatch() {
		int value = MatcherUtil.doExactMatch("dinesh karuppiah", "dinesh karuppiah");
		assertEquals(100, value);
	}

	@Test
	public void TestInvalidExactMatch() {
		int value = MatcherUtil.doExactMatch("dinesh k", "dinesh karuppiah");
		assertNotEquals(100, value);
	}
	
	@Test
	public void TestInvalidExactMatchwithEmpty() {
		int value = MatcherUtil.doExactMatch("Dinesh", "Karuppiah");
		assertEquals(0, value);
	}

	@Test
	public void TestValidPartialMatch() {
		int value = MatcherUtil.doPartialMatch("dinesh k", "dinesh karuppiah");
		assertEquals(50, value);
	}

	@Test
	public void TestInvalidPartialMatch() {
		int value = MatcherUtil.doPartialMatch("Dinesh Karuppiah", "Thiagarajan");
		assertNotEquals(50, value);
	}

	@Test
	public void TestvalidLessThanEqualToMatch() {
		int value = MatcherUtil.doLessThanEqualToMatch(18, 20);
		assertEquals(100, value);
	}

	@Test
	public void TestInvalidLessThanEqualToMatch() {
		int value = MatcherUtil.doLessThanEqualToMatch(80, 20);
		assertNotEquals(100, value);
	}

	@Test
	public void TestValidDateExactMatch() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
		String dateInString = "10/08/2018";
		Date date = sdf.parse(dateInString);
		int value = MatcherUtil.doExactMatch(date, date);
		assertEquals(100, value);
	}

	@Test
	public void TestInvalidDateExactMatch() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
		String dateInString = "10/08/2018";
		Date date = sdf.parse(dateInString);
		int value = MatcherUtil.doExactMatch(date, new Date());
		assertNotEquals(100, value);
	}

	@Test
	public void TestPhoneticsMatch() {
		int value = MatcherUtil.doPhoneticsMatch("", "");
		assertEquals(0, value);
	}

}
