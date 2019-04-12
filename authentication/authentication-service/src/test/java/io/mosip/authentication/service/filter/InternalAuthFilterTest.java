package io.mosip.authentication.service.filter;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class InternalAuthFilterTest {

	InternalAuthFilter internalAuthFilter = new InternalAuthFilter();
	
	Map<String, Object> requestBody = new HashMap<>();
	
	@Test
	public void testValidateDecipherRequest() throws IdAuthenticationAppException {
		internalAuthFilter.decipherRequest(requestBody);
	}

}
