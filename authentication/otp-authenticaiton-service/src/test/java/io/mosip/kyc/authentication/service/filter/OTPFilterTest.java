package io.mosip.kyc.authentication.service.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.service.filter.OTPFilter;

public class OTPFilterTest {

	OTPFilter filter = new OTPFilter();
	
	@Test
	public void testSign() throws IdAuthenticationAppException {
		assertEquals(true, filter.validateSignature("something", "something".getBytes()));
	}
}
