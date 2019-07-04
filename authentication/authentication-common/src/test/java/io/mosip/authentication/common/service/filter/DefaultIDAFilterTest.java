package io.mosip.authentication.common.service.filter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.filter.DefaultIDAFilter;
import io.mosip.authentication.common.service.filter.ResettableStreamHttpServletRequest;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class DefaultIDAFilterTest {

	@Mock
	ResettableStreamHttpServletRequest requestWrapper;

	DefaultIDAFilter defaultIDAFilter = new DefaultIDAFilter();

	@Test
	public void authenticateRequestTest() throws IdAuthenticationAppException {
		defaultIDAFilter.authenticateRequest(requestWrapper);
	}

}
