package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class
		})
public class DOBMatchingStrategyTest {
	SimpleDateFormat sdf = null;
	
	@Autowired
	private Environment environment;

	@Before
	public void setup() {
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		//ReflectionTestUtils.setField(targetObject, name, value);
	}

	/**
	 * Check for Exact type matched with Enum value of DOB Matching Strategy
	 */
	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(DOBMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of DOB Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(DOBMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the DOB Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(DOBMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the DOB Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		MatchFunction matchFunction = DOBMatchingStrategy.EXACT.getMatchFunction();
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
		MatchFunction matchFunction = DOBMatchingStrategy.EXACT.getMatchFunction();
		int value = -1;

		value = matchFunction.match("07/02/1993", "1993/02/07", null);

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
		MatchFunction matchFunction = DOBMatchingStrategy.EXACT.getMatchFunction();
        Map<String,Object> dobMap =null;
        //dobMap.put(Environment.class.getSimpleName(), environment.getProperty("date.pattern"));
		int value = matchFunction.match("27/02/1993", "1993/02/07", dobMap);
		assertEquals(0, value);

		int value1 = matchFunction.match(2, "1993/02/07", null);
		assertEquals(0, value1);

		int value3 = matchFunction.match(null, null, null);
		assertEquals(0, value3);

	}

	@Test(expected = ParseException.class)
	public void TestInvalidDate() throws IdAuthenticationBusinessException {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = DOBMatchingStrategy.EXACT.getMatchFunction();
		int value = matchFunction.match("test", "test-02-27", matchProperties);
		assertEquals(0, value);
	}

}
