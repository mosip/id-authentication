package io.mosip.authentication.service.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;

public class OTPFilterTest {

	OTPFilter filter = new OTPFilter();
	
	@Test
	public void testSign() throws IdAuthenticationAppException {
		assertEquals(true, filter.validateSignature("something", "something".getBytes()));
	}
}
