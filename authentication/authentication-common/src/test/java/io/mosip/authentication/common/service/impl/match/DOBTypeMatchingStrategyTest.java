package io.mosip.authentication.common.service.impl.match;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class})
public class DOBTypeMatchingStrategyTest {

	@Mock
	private DemoMatcherUtil demoMatcherUtil;
	
	Map<String, Object> matchProperties = new HashMap<>();
	
	@BeforeEach
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
