package io.mosip.authentication.otp.service.filter;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.filter.IdAuthFilter;

// TODO: Auto-generated Javadoc
/**
 * The Class OTPFilterTest.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { IdAuthFilter.class })
@WebMvcTest 
public class OTPFilterTest {

	/** The filter. */
	OTPFilter filter = new OTPFilter();
	
	
	/** The environment. */
	@Autowired
	private Environment environment;
	
	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;
	
	
	
	/**
	 * Before.
	 */
	@Before
	public void before() {
		ReflectionTestUtils.setField(filter, "mapper", mapper);
		ReflectionTestUtils.setField(filter, "env", environment);
	}


	
	/**
	 * Test OTP request not allowed as per policy.
	 */
	@Test
	public void testOTPRequestNotAllowed() {
		String errorMessage="IDA-MPA-005 --> OTP Request Usage not allowed as per policy";
		Map<String,Object> reqMap=new HashMap<>();
	try {	
		ReflectionTestUtils.invokeMethod(filter, "checkAllowedAuthTypeBasedOnPolicy", "9903348702934",reqMap);
	}
	catch(UndeclaredThrowableException ex) {
		    assertTrue(ex.getCause().getMessage().equalsIgnoreCase(errorMessage));
	}
	}
}
