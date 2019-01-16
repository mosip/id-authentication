package io.mosip.authentication.service.filter;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;

public class OTPFilterTest {

	OTPFilter filter = new OTPFilter();
	
	Map<String, Object> requestBody = new HashMap<>();

	Map<String, Object> responseBody = new HashMap<>();;
	
	@Test
	public void testSetTxnId() throws IdAuthenticationAppException {
		responseBody.put("txnId", "1234");
		assertEquals(responseBody, filter.setResponseParam(requestBody, responseBody));
	}
	
	@Test
	public void testDecodedRequest() throws IdAuthenticationAppException {
		assertEquals(responseBody, filter.decodedRequest(responseBody));
	}
	
	@Test
	public void testEncodedResponse() throws IdAuthenticationAppException {
		assertEquals(responseBody, filter.encodedResponse(responseBody));
	}
}
