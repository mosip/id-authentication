package io.mosip.authentication.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.EncoderException;
import org.hamcrest.core.Is;
import org.junit.Test;

/**
 * 
 * @author Dinesh Karuppiah
 */
public class MatcherUtilTest {

	/**
	 * Assert entity info matched with ref info via doExactMatch
	 */
	@Test
	public void TestValiddoExactMatch() {
		int value = DemoMatcherUtil.doExactMatch("dinesh karuppiah", "dinesh karuppiah");
		assertEquals(100, value);
	}

	/**
	 * Assert entity info not matched with ref info via doExactMatch
	 */
	@Test
	public void TestInvalidExactMatch() {
		int value = DemoMatcherUtil.doExactMatch("dinesh k", "dinesh karuppiah");
		assertNotEquals(100, value);
	}

	/**
	 * Assert entity info not matched with ref info via doExactMatch
	 */
	@Test
	public void TestInvalidExactMatchwithEmpty() {
		int value = DemoMatcherUtil.doExactMatch("Dinesh", "Karuppiah");
		assertEquals(0, value);
	}

	/**
	 * Assert entity info not matched with ref info as Emtpy
	 */
	@Test
	public void TestInvalidExactMatchwithEmptyvalue() {
		int value = DemoMatcherUtil.doExactMatch("", "Karuppiah");
		assertEquals(0, value);
	}

	/**
	 * Assert partial match with entity and ref info details
	 */
	@Test
	public void TestValidPartialMatch() {
		int value = DemoMatcherUtil.doPartialMatch("dinesh k", "dinesh karuppiah");
		assertEquals(50, value);
	}

	/**
	 * Assert partial match with entity and ref info details
	 */
	@Test
	public void TestValidPartialMatchwithInvalidvalues() {
		int value = DemoMatcherUtil.doPartialMatch("dinesh k", "dinesh thiagarajan");
		assertEquals(33, value);
	}

	/**
	 * Assert partial match - entity info not matched with ref info details
	 */
	@Test
	public void TestInvalidPartialMatch() {
		int value = DemoMatcherUtil.doPartialMatch("Dinesh Karuppiah", "Thiagarajan");
		assertNotEquals(50, value);
	}

	/**
	 * Assert do less than equal matched - for age
	 * 
	 */
	@Test
	public void TestvalidLessThanEqualToMatch() {
		int value = DemoMatcherUtil.doLessThanEqualToMatch(18, 20);
		assertEquals(100, value);
	}

	/**
	 * Assert do less than equal match not matched- for age
	 */
	@Test
	public void TestInvalidLessThanEqualToMatch() {
		int value = DemoMatcherUtil.doLessThanEqualToMatch(80, 20);
		assertNotEquals(100, value);
	}

	/**
	 * Assert do exact match matched- for Date param
	 */
	@Test
	public void TestValidDateExactMatch() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
		String dateInString = "10/08/2018";
		Date date = sdf.parse(dateInString);
		int value = DemoMatcherUtil.doExactMatch(date, date);
		assertEquals(100, value);
	}

	/**
	 * Assert do exact match not-matched- for Date param
	 */
	@Test
	public void TestInvalidDateExactMatch() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
		String dateInString = "10/08/2018";
		Date date = sdf.parse(dateInString);
		int value = DemoMatcherUtil.doExactMatch(date, new Date());
		assertNotEquals(100, value);
	}
	
	@Test
	public void testDoPhoneticsMatch_Exact() throws EncoderException {
		String refInfoName = "فاس-الدار البيضاء";
		String entityInfoName = "فاس-الدار البيضاء";
		String language = "arabic";
		int value = DemoMatcherUtil.doPhoneticsMatch(refInfoName, entityInfoName, language);
		assertEquals(100, value);
	}
	
	@Test
	public void testDoPhoneticsMatch_Partial() throws EncoderException {
		String refInfoName = "فاس-الدار البيضاء";
		String entityInfoName = "-الدار البيضاء";
		String language = "arabic";
		int value = DemoMatcherUtil.doPhoneticsMatch(refInfoName, entityInfoName, language);
		assertNotEquals(100, value);
	}
}
