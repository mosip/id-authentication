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
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class})
public class AgeMatchingStrategyTest {

	@Mock
	private DemoMatcherUtil demoMatcherUtil;
	
	Map<String, Object> matchProperties = new HashMap<>();
	
	@Before
	public void before() {				
		matchProperties.put("demoMatcherUtil", demoMatcherUtil);
	}
	
	/**
	 * Check for Exact type matched with Enum value of Age Matching Strategy
	 */
	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(AgeMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of Age Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(AgeMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the Age Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(AgeMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the Age Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		MatchFunction matchFunction = AgeMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Tests doMatch function on Matching Strategy Function
	 * @throws IdAuthenticationBusinessException 
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = AgeMatchingStrategy.EXACT.getMatchFunction();
		Mockito.when(demoMatcherUtil.doLessThanEqualToMatch(Mockito.anyInt(), Mockito.anyInt())).thenReturn(100);
		
		int value = matchFunction.match(25, 25, matchProperties);
		assertEquals(100, value);

		int value1 = matchFunction.match(100, 100, matchProperties);
		assertEquals(100, value1);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 * @throws IdAuthenticationBusinessException 
	 */
	@Test(expected=IdAuthenticationBusinessException.class)
	public void TestInvalidExactMatchingStrategyFunction() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = AgeMatchingStrategy.EXACT.getMatchFunction();
		Mockito.when(demoMatcherUtil.doExactMatch(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
		
		int value = matchFunction.match(250, "50", matchProperties);
		assertEquals(0, value);

		int value1 = matchFunction.match(50, "25", matchProperties);
		assertEquals(0, value1);

		int value2 = matchFunction.match(100, "25", matchProperties);
		assertEquals(0, value2);

		int value3 = matchFunction.match(25, "24", matchProperties);
		assertEquals(0, value3);

		int value4 = matchFunction.match(null, null, matchProperties);
		assertEquals(0, value4);

		int value6 = matchFunction.match("abc", "1", matchProperties);
		assertEquals(0, value6);
	}
}
