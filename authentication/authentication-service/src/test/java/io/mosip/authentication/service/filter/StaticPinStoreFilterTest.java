package io.mosip.authentication.service.filter;

import static org.junit.Assert.assertEquals;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * 
 * @author Prem Kumar
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IdTemplateManager.class,
		TemplateManagerBuilderImpl.class })
public class StaticPinStoreFilterTest {

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "datetime.pattern";

	StaticPinStoreFilter filter = new StaticPinStoreFilter();

	Map<String, Object> requestBody = new HashMap<>();

	Map<String, Object> responseBody = new HashMap<>();

	@Autowired
	private ObjectMapper mapper;

	/** The env. */
	@Autowired
	Environment env;

	@Before
	public void before() {
		ReflectionTestUtils.setField(filter, "env", env);
		ReflectionTestUtils.setField(filter, "mapper", mapper);
	}

	@Test
	public void testsetResponseParam_Success() throws IdAuthenticationAppException {

		String reqTime = DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN));
		requestBody.put("reqTime", reqTime);
		responseBody.put("resTime", reqTime);
		assertEquals(responseBody, filter.setResponseParam(requestBody, responseBody));
	}

	@Test
	public void testsetResponseParam_NullReqTime() throws IdAuthenticationAppException {
		assertEquals(responseBody, filter.setResponseParam(requestBody, responseBody));
	}

	@Test
	public void testEncodedResponse() throws IdAuthenticationAppException {
		assertEquals(responseBody, filter.encodedResponse(responseBody));
	}

	@Test
	public void testDecodedRequest() throws IdAuthenticationAppException {
		assertEquals(responseBody, filter.decodedRequest(requestBody));
	}
}
