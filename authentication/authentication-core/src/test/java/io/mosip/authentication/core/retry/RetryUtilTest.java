package io.mosip.authentication.core.retry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.retry.RetryConfig;
import io.mosip.authentication.core.retry.RetryListenerImpl;
import io.mosip.authentication.core.util.RetryUtil;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, RetryConfig.class,
		RetryListenerImpl.class, RetryUtil.class })
public class RetryUtilTest {

	@Autowired
	RetryUtil retryUtil;

	@Autowired
	Environment env;

	@Test
	public void testRetryPolicy_Testsuccess() throws Exception {
		FailingMockOperation<?> failingMockOperation = new FailingMockOperation<>(() -> new IOException());
		Object result = retryUtil.doWithRetry(failingMockOperation::get);
		assertNotNull(result);
	}

	@Test(expected = IOException.class)
	public void testRetryPolicy_TestFailureWithRetryableException() throws Exception {
		Integer retryLimit = env.getProperty(IdAuthConfigKeyConstants.IDA_RETRY_SIMPLE_LIMIT, Integer.class);
		FailingMockOperation<IOException> failingMockOperation = new FailingMockOperation<>(retryLimit + 10, () -> new IOException());
		Object result;
		try {
			result = retryUtil.doWithRetry(failingMockOperation::get);
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
			result = retryUtil.doWithRetry(failingMockOperation::get);
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
		Object result = retryUtil.doWithRetry(failingMockOperation::get);
		assertEquals(failingMockOperation.getExecutedTimes(), retryCount + 1);
		assertNotNull(result);

	}
	
	@Test
	public void testRetryPolicy_TestSuccessAfterFailuresTillMaxRetries() throws Exception {
		Integer retryLimit = env.getProperty(IdAuthConfigKeyConstants.IDA_RETRY_SIMPLE_LIMIT, Integer.class);
		FailingMockOperation<?> failingMockOperation = new FailingMockOperation<>(retryLimit,
				() -> new IOException());
		Object result = retryUtil.doWithRetry(failingMockOperation::get);
		assertEquals(failingMockOperation.getExecutedTimes(), retryLimit + 1);
		assertNotNull(result);

	}
	
	@Test
	public void testRetryPolicy_TestsuccessWithRunnable() throws Exception {
		FailingMockOperation<?> failingMockOperation = new FailingMockOperation<>(() -> new IOException());
		retryUtil.doWithRetry(failingMockOperation::run);
	}
	
	@Test(expected = IOException.class)
	public void testRetryPolicy_TestFailureWithRunnable() throws Exception {
		Integer retryLimit = env.getProperty(IdAuthConfigKeyConstants.IDA_RETRY_SIMPLE_LIMIT, Integer.class);
		FailingMockOperation<IOException> failingMockOperation = new FailingMockOperation<>(retryLimit + 10, () -> new IOException());
		Object result;
		try {
			retryUtil.doWithRetry(failingMockOperation::run);
		} catch (IOException e) {
			assertEquals(failingMockOperation.getExecutedTimes(), retryLimit + 1);
			throw e;
		}
		fail();
	}
	
	@Test
	public void testRetryPolicy_TestsuccessWithConsumer() throws Exception {
		FailingMockOperation<?> failingMockOperation = new FailingMockOperation<>(() -> new IOException());
		retryUtil.doWithRetry(failingMockOperation::accept, "Hello");
	}
	
	@Test(expected = IOException.class)
	public void testRetryPolicy_TestFailureWithConsumer() throws Exception {
		Integer retryLimit = env.getProperty(IdAuthConfigKeyConstants.IDA_RETRY_SIMPLE_LIMIT, Integer.class);
		FailingMockOperation<IOException> failingMockOperation = new FailingMockOperation<>(retryLimit + 10, () -> new IOException());
		Object result;
		try {
			retryUtil.doWithRetry(failingMockOperation::accept, "Hello");
		} catch (IOException e) {
			assertEquals(failingMockOperation.getExecutedTimes(), retryLimit + 1);
			throw e;
		}
		fail();
	}
	
	@Test
	public void testRetryPolicy_TestsuccessWithFunction() throws Exception {
		FailingMockOperation<?> failingMockOperation = new FailingMockOperation<>(() -> new IOException());
		Object result = retryUtil.doWithRetry(failingMockOperation::apply, "Hello");
		assertEquals(result, "Hello");
	}

	@Test(expected = IOException.class)
	public void testRetryPolicy_TestFailureWithFunction() throws Exception {
		Integer retryLimit = env.getProperty(IdAuthConfigKeyConstants.IDA_RETRY_SIMPLE_LIMIT, Integer.class);
		FailingMockOperation<IOException> failingMockOperation = new FailingMockOperation<>(retryLimit + 10, () -> new IOException());
		Object result;
		try {
			result = retryUtil.doWithRetry(failingMockOperation::apply, "Hello");
		} catch (IOException e) {
			assertEquals(failingMockOperation.getExecutedTimes(), retryLimit + 1);
			throw e;
		}
		fail();
	}

}