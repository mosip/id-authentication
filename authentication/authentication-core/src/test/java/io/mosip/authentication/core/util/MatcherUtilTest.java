package io.mosip.authentication.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.EncoderException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.kernel.demographics.spi.IDemoApi;

/**
 * 
 * @author Dinesh Karuppiah
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class,  })
public class MatcherUtilTest {

	@InjectMocks
	DemoMatcherUtil demoMatcherUtil;
	
	@Mock
	IDemoApi demoApi;
	
	@Before
	public void before() {
		//demoApi = Mockito.mock(IDemoApi.class);
	}
	/**
	 * Assert entity info matched with ref info via doExactMatch
	 */
	@Test
	public void TestValiddoExactMatch() {
		Mockito.when(demoApi.doExactMatch(Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(100);
		int value = demoMatcherUtil.doExactMatch("dinesh karuppiah", "dinesh karuppiah");
		assertEquals(100, value);
	}

	/**
	 * Assert entity info not matched with ref info via doExactMatch
	 */
	@Test
	public void TestInvalidExactMatch() {
		int value = demoMatcherUtil.doExactMatch("dinesh k", "dinesh karuppiah");
		assertNotEquals(100, value);
	}

	/**
	 * Assert entity info not matched with ref info via doExactMatch
	 */
	@Test
	public void TestInvalidExactMatchwithEmpty() {		
		int value = demoMatcherUtil.doExactMatch("Dinesh", "Karuppiah");
		assertEquals(0, value);
	}

	/**
	 * Assert entity info not matched with ref info as Emtpy
	 */
	@Test
	public void TestInvalidExactMatchwithEmptyvalue() {
		int value = demoMatcherUtil.doExactMatch("", "Karuppiah");
		assertEquals(0, value);
	}

	/**
	 * Assert partial match with entity and ref info details
	 */
	@Test
	public void TestValidPartialMatch() {
		Mockito.when(demoApi.doPartialMatch(Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(50);
		int value = demoMatcherUtil.doPartialMatch("dinesh k", "dinesh karuppiah");
		assertEquals(50, value);
	}

	/**
	 * Assert partial match with entity and ref info details
	 */
	@Test
	public void TestValidPartialMatchwithInvalidvalues() {
		Mockito.when(demoApi.doPartialMatch(Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(33);
		int value = demoMatcherUtil.doPartialMatch("dinesh k", "dinesh thiagarajan");
		assertEquals(33, value);
	}

	/**
	 * Assert partial match - entity info not matched with ref info details
	 */
	@Test
	public void TestInvalidPartialMatch() {
		int value = demoMatcherUtil.doPartialMatch("Dinesh Karuppiah", "Thiagarajan");
		assertNotEquals(50, value);
	}

	/**
	 * Assert do less than equal matched - for age
	 * 
	 */
	@Test
	public void TestvalidLessThanEqualToMatch() {
		int value = demoMatcherUtil.doLessThanEqualToMatch(18, 20);
		assertEquals(100, value);
	}

	/**
	 * Assert do less than equal match not matched- for age
	 */
	@Test
	public void TestInvalidLessThanEqualToMatch() {
		int value = demoMatcherUtil.doLessThanEqualToMatch(80, 20);
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
		int value = demoMatcherUtil.doExactMatch(date, date);
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
		int value = demoMatcherUtil.doExactMatch(date, new Date());
		assertNotEquals(100, value);
	}
	
	@Test
	public void testDoPhoneticsMatch_Exact() throws EncoderException {
		String refInfoName = "فاس-الدار البيضاء";
		String entityInfoName = "فاس-الدار البيضاء";
		String language = "arabic";
		Mockito.when(demoApi.doPhoneticsMatch(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(100);
		int value = demoMatcherUtil.doPhoneticsMatch(refInfoName, entityInfoName, language);
		assertEquals(100, value);
	}
	
	@Test
	public void testDoPhoneticsMatch_Partial() throws EncoderException {
		String refInfoName = "فاس-الدار البيضاء";
		String entityInfoName = "-الدار البيضاء";
		String language = "arabic";
		Mockito.when(demoApi.doPhoneticsMatch(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(60);
		int value = demoMatcherUtil.doPhoneticsMatch(refInfoName, entityInfoName, language);
		assertNotEquals(100, value);
	}
}
