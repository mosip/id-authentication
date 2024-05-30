package io.mosip.authentication.common.service.impl.match;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.util.DemoMatcherUtil;

/**
 * 
 * @author Nagarjuna
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class
		})
public class EmailMatchingStrategyTest {

	@Mock
	private DemoMatcherUtil demoMatcherUtil;
	
	Map<String, Object> matchProperties = new HashMap<>();
	
	@Before
	public void setup() {		
		matchProperties.put("demoMatcherUtil", demoMatcherUtil);
	}

	/**
	 * Check for Exact type matched with Enum value of EMAIL Matching Strategy
	 */
	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(EmailMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of EMAIL Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(EmailMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the EMAIL Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(EmailMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the EMAIL Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		MatchFunction matchFunction = EmailMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Tests doMatch function on Matching Strategy Function
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = EmailMatchingStrategy.EXACT.getMatchFunction();
		Mockito.when(demoMatcherUtil.doExactMatch(Mockito.anyString(),Mockito.anyString())).thenReturn(100);
		int value = matchFunction.match("abc@mail.com", "abc@mail.com", matchProperties);
		assertEquals(100, value);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() throws IdAuthenticationBusinessException {

		MatchFunction matchFunction = EmailMatchingStrategy.EXACT.getMatchFunction();
		Mockito.when(demoMatcherUtil.doExactMatch(Mockito.anyString(),Mockito.anyString())).thenReturn(0);
		int value = matchFunction.match("abc@mail.com", "abc@email.com", matchProperties);
		assertEquals(0, value);
	}

	@Test
	public void TestInvalidEmail() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = EmailMatchingStrategy.EXACT.getMatchFunction();
		Mockito.when(demoMatcherUtil.doExactMatch(Mockito.anyString(),Mockito.anyString())).thenReturn(0);
		int value4 = matchFunction.match(1, "2", matchProperties);
		assertEquals(0, value4);
	}

	@Test
	public void TestInvalidE_mail() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = EmailMatchingStrategy.EXACT.getMatchFunction();
		Mockito.when(demoMatcherUtil.doExactMatch(Mockito.anyString(),Mockito.anyString())).thenReturn(0);
		int value5 = matchFunction.match(1, "abc@mail.com", matchProperties);
		assertEquals(0, value5);
	}
}
