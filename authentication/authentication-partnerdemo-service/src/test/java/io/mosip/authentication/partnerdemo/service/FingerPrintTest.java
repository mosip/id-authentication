package io.mosip.authentication.partnerdemo.service;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.partnerdemo.service.controller.FingerPrint;

/**
 * @author Arun Bose S The Class FingerPrintTest.
 */
@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class FingerPrintTest {

	/** The finger print mock. */
	@InjectMocks
	private FingerPrint fingerPrintMock;

	/**
	 * Finger print test.
	 *
	 * @throws KeyManagementException   the key management exception
	 * @throws RestClientException      the rest client exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws JSONException            the JSON exception
	 */
	@Test
	public void fingerPrintTest()
			throws KeyManagementException, RestClientException, NoSuchAlgorithmException, JSONException {
		fingerPrintMock.fingerprint();
	}

}
