package io.mosip.authentication.core.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, RetryConfig.class,
		RetryListenerImpl.class })
public class RetryConfigTest {

	@Autowired
	RetryTemplate retryTemplate;

	@Autowired
	Environment env;

	@Test
	public void testRetryPolicy_Testsuccess() throws Exception {
		FailingMockOperation<?> failingMockOperation = new FailingMockOperation<>(0, () -> new IOException());
		Object result = retryTemplate.execute(c -> failingMockOperation.get());
		assertNotNull(result);
	}

	@Test(expected = IOException.class)
	public void testRetryPolicy_TestFailureWithRetryableException() throws Exception {
		Integer retryLimit = env.getProperty(IdAuthConfigKeyConstants.IDA_RETRY_SIMPLE_LIMIT, Integer.class);
		FailingMockOperation<IOException> failingMockOperation = new FailingMockOperation<>(retryLimit + 10, () -> new IOException());
		Object result;
		try {
			result = retryTemplate.execute(c -> failingMockOperation.get());
		} catch (IOException e) {
			assertEquals(failingMockOperation.getExecutedTimes(), retryLimit + 1);
			throw e;
		}
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRetryPolicy_TestFailureWithNonRetryableException() throws Exception {
		Integer retryLimit = env.getProperty("ida.retry.simple.limit", Integer.class);
		FailingMockOperation<IllegalArgumentException> failingMockOperation = new FailingMockOperation<>(retryLimit + 10,
				() -> new IllegalArgumentException());
		Object result;
		try {
			result = retryTemplate.execute(c -> failingMockOperation.get());
		} catch (IllegalArgumentException e) {
			assertEquals(failingMockOperation.getExecutedTimes(), 1);
			throw e;
		}
		fail();
	}

	@Test
	public void testRetryPolicy_TestSuccessAfterfewFailures() throws Exception {
		Integer retryLimit = env.getProperty(IdAuthConfigKeyConstants.IDA_RETRY_SIMPLE_LIMIT, Integer.class);
		int retryCount = retryLimit / 2;
		FailingMockOperation<?> failingMockOperation = new FailingMockOperation<>(retryCount,
				() -> new IOException());
		Object result = retryTemplate.execute(c -> failingMockOperation.get());
		assertEquals(failingMockOperation.getExecutedTimes(), retryCount + 1);
		assertNotNull(result);

	}
	
	@Test
	public void testRetryPolicy_TestSuccessAfterFailuresTillMaxRetries() throws Exception {
		Integer retryLimit = env.getProperty(IdAuthConfigKeyConstants.IDA_RETRY_SIMPLE_LIMIT, Integer.class);
		FailingMockOperation<?> failingMockOperation = new FailingMockOperation<>(retryLimit,
				() -> new IOException());
		Object result = retryTemplate.execute(c -> failingMockOperation.get());
		assertEquals(failingMockOperation.getExecutedTimes(), retryLimit + 1);
		assertNotNull(result);

	}

}