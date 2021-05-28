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
 * The Class DOBTypeMatchingStrategyTest.
 *
 * @author Manoj SP
 * @author Nagarjuna
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class})
public class DOBTypeMatchingStrategyTest {

	@Mock
	private DemoMatcherUtil demoMatcherUtil;
	
	Map<String, Object> matchProperties = new HashMap<>();
	
	@Before
	public void before() {				
		matchProperties.put("demoMatcherUtil", demoMatcherUtil);
	}
	
	/**
	 * Check for Exact type matched with Enum value of DOB Type Matching Strategy.
	 */
	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(DOBTypeMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of DOB Type Matching
	 * Strategy.
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(DOBTypeMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the DOB Type Matching Strategy for Exact is Not null.
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(DOBTypeMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the DOB Type Matching Strategy for Exact is null.
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		MatchFunction matchFunction = DOBTypeMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Tests doMatch function on Matching Strategy Function.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = DOBTypeMatchingStrategy.EXACT.getMatchFunction();
		int value = -1;
		Mockito.when(demoMatcherUtil.doExactMatch(Mockito.anyString(), Mockito.anyString())).thenReturn(100);
		value = matchFunction.match("V", "V", matchProperties);

		assertEquals(100, value);
	}

	/**
	 * Tests the Match function with in-valid values.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() throws IdAuthenticationBusinessException {

		MatchFunction matchFunction = DOBTypeMatchingStrategy.EXACT.getMatchFunction();
		Mockito.when(demoMatcherUtil.doLessThanEqualToMatch(Mockito.anyInt(), Mockito.anyInt())).thenReturn(0);
		int value = matchFunction.match(332, "V", matchProperties);
		assertEquals(0, value);

		int value1 = matchFunction.match("A", "V", matchProperties);
		assertEquals(0, value1);

		int value3 = matchFunction.match(null, null, matchProperties);
		assertEquals(0, value3);

	}

}
